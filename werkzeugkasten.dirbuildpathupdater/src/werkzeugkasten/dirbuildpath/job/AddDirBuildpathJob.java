package werkzeugkasten.dirbuildpath.job;

import static werkzeugkasten.dirbuildpath.Constants.NO_ACCESS_RULES;
import static werkzeugkasten.dirbuildpath.Constants.NO_EXTRA_ATTRIBUTES;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPersistentPreferenceStore;

import werkzeugkasten.common.jdt.ClasspathEntryUtil;
import werkzeugkasten.common.runtime.AdaptableUtil;
import werkzeugkasten.common.runtime.StatusUtil;
import werkzeugkasten.common.ui.ProgressMonitorUtil;
import werkzeugkasten.common.viewers.AbstractLightweightLabelDecorator;
import werkzeugkasten.dirbuildpath.Activator;
import werkzeugkasten.dirbuildpath.Constants;
import werkzeugkasten.dirbuildpath.nls.Strings;

public class AddDirBuildpathJob extends WorkspaceJob {

	protected List<IPath> pathList;
	protected IJavaProject project;
	protected IPersistentPreferenceStore pref;
	protected boolean forceupdate;

	public AddDirBuildpathJob(IJavaProject project,
			IPersistentPreferenceStore store, IPath... path) {
		this(project, store, Arrays.asList(path));
	}

	public AddDirBuildpathJob(IJavaProject project,
			IPersistentPreferenceStore store, List<IPath> pathList) {
		super(Strings.UPDATE_BUILDPATH_CONTAINER);
		this.pathList = pathList;
		this.project = project;
		this.pref = store;
	}

	@Override
	public IStatus runInWorkspace(final IProgressMonitor monitor)
			throws CoreException {
		monitor.beginTask(Strings.UPDATE_BUILDPATH_CONTAINER,
				IProgressMonitor.UNKNOWN);
		try {
			Map<IPath, IClasspathEntry> ents = ClasspathEntryUtil
					.toClasspathMap(this.project);
			for (IPath path : this.pathList) {
				Map<String, IClasspathEntry> map = computeEntries(path);
				ProgressMonitorUtil.isCanceled(monitor, 1);
				for (IClasspathEntry ce : map.values()) {
					IPath p = ce.getPath();
					if (this.forceupdate || ents.containsKey(p) == false) {
						ents.put(p, ce);
					}
				}
				ProgressMonitorUtil.isCanceled(monitor, 1);
				this.pref.setValue(path.toString(), "true");
			}
			Collection<IClasspathEntry> c = ents.values();
			IClasspathEntry[] ary = c.toArray(new IClasspathEntry[c.size()]);
			this.project.setRawClasspath(ary, monitor);
			this.pref.save();

			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			for (IPath p : this.pathList) {
				AbstractLightweightLabelDecorator.updateDecorators(
						Constants.ID_DECORATOR, root.findMember(p));
			}
			return Status.OK_STATUS;
		} catch (IOException e) {
			throw new CoreException(StatusUtil.createError(Activator
					.getDefault(), e));
		} finally {
			monitor.done();
		}
	}

	public static final Pattern isLib = Pattern.compile(".+\\.(zip|jar)",
			Pattern.CASE_INSENSITIVE);
	protected static final Pattern isSrc = Pattern.compile(".*(\\.src|-sources).*",
			Pattern.CASE_INSENSITIVE);
	protected static final Pattern isDoc = Pattern.compile(".*(\\.doc|-javadoc).*",
			Pattern.CASE_INSENSITIVE);

	protected static Map<String, IClasspathEntry> computeEntries(IPath path)
			throws CoreException {
		final Map<String, IClasspathEntry> result = new TreeMap<String, IClasspathEntry>();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource rootDir = root.findMember(path);
		IContainer c = AdaptableUtil.to(rootDir, IContainer.class);
		if (c != null) {
			c.accept(new IResourceVisitor() {
				public boolean visit(IResource resource) throws CoreException {
					IPath full = resource.getFullPath();
					if (isSrc.matcher(full.toString()).matches() == false
							&& isDoc.matcher(full.toString()).matches() == false
							&& resource.getType() == IResource.FILE
							&& isLib.matcher(full.lastSegment()).matches()) {
						IPath src = findSource(full);
						IClasspathEntry ce = JavaCore.newLibraryEntry(full,
								src, new Path("."), NO_ACCESS_RULES,
								NO_EXTRA_ATTRIBUTES, true);
						result.put(full.toString(), ce);
					}
					return true;
				}
			});
		}
		return result;
	}

	protected static IPath findSource(IPath lib) {
		String[] fix = { "src", "sources" };
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (IPath parent : parents(fix, lib)) {
			for (IPath name : names(fix, lib)) {
				IResource r = root.findMember(parent.append(name));
				if (r != null && r.exists()) {
					return r.getFullPath();
				}
			}
		}
		return null;
	}

	protected static List<IPath> parents(String[] sufix, IPath lib) {
		IPath parent = lib.removeLastSegments(1);
		List<IPath> parents = new ArrayList<IPath>();
		parents.add(parent);
		for (String s : sufix) {
			parents.add(parent.append(s));
		}
		return parents;
	}

	protected static List<IPath> names(String[] sufix, IPath lib) {
		String base = lib.removeFileExtension().lastSegment();
		String[] sign = { ".", "-" };
		String[] ext = { "jar", "zip" };
		List<IPath> names = new ArrayList<IPath>();
		for (String s : sign) {
			String signed = base + s;
			for (String f : sufix) {
				String fixed = signed + f;
				for (String e : ext) {
					IPath filename = new Path(fixed).addFileExtension(e);
					names.add(filename);
				}
			}
		}
		return names;
	}

}
