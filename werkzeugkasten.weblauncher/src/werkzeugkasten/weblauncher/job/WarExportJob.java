package werkzeugkasten.weblauncher.job;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.ui.jarpackager.IJarBuilder;
import org.eclipse.jdt.ui.jarpackager.JarPackageData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.progress.WorkbenchJob;

import werkzeugkasten.common.ui.ProgressMonitorUtil;
import werkzeugkasten.common.util.StringUtil;
import werkzeugkasten.weblauncher.Activator;
import werkzeugkasten.weblauncher.Constants;
import werkzeugkasten.weblauncher.nls.Strings;
import werkzeugkasten.weblauncher.preferences.WebPreferences;

public class WarExportJob extends WorkbenchJob {

	protected IProject project;

	public WarExportJob(IProject project) {
		super(Strings.MSG_PROCESS_EXPORT);
		this.project = project;
	}

	public IStatus runInUIThread(final IProgressMonitor monitor) {
		monitor.beginTask(Strings.MSG_PROCESS_EXPORT, IProgressMonitor.UNKNOWN);
		try {
			WebPreferences pref = Activator.getPreferences(project);
			JarPackageData data = setUp(project, pref);

			final MultiStatus status = new MultiStatus(Constants.ID_PLUGIN,
					IStatus.ERROR, Strings.MSG_EXPORT_ERRORS, null);
			final IJarBuilder builder = data.createFatJarBuilder();
			try {
				data.setElements(new Object[] { project }); // dummy data.
				builder.open(data, new Shell(getDisplay()), status);

				ProgressMonitorUtil.isCanceled(monitor, 1);

				addEntries(monitor, pref, data, builder);

				ProgressMonitorUtil.isCanceled(monitor, 1);
				IStatus[] kids = status.getChildren();
				if (kids != null && 0 < kids.length) {
					Activator.getDefault().getLog().log(status);
				}
				ProgressMonitorUtil.isCanceled(monitor, 1);
			} finally {
				builder.close();
			}
		} catch (OperationCanceledException e) {
			throw e;
		} catch (Exception e) {
			Activator.log(e);
		} finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}

	protected void addEntries(final IProgressMonitor monitor,
			WebPreferences pref, JarPackageData data, final IJarBuilder builder)
			throws CoreException {
		IPath basePath = new Path(pref.getBaseDir());
		IResource base = ResourcesPlugin.getWorkspace().getRoot().findMember(
				basePath);
		final int baseSegment = basePath.segmentCount();
		if (base != null && base.exists()) {
			final Pattern ignore = pref.getExportIgnore();
			base.accept(new IResourceVisitor() {
				public boolean visit(IResource resource) throws CoreException {
					ProgressMonitorUtil.isCanceled(monitor, 1);
					String name = resource.getName();
					if (ignore.matcher(name).matches()) {
						return false;
					}
					if (resource.getType() == IResource.FILE) {
						IFile f = (IFile) resource;
						IPath dest = f.getFullPath().removeFirstSegments(
								baseSegment);
						builder.writeFile(f, dest);
					}
					return true;
				}
			});
		}
	}

	protected JarPackageData setUp(IProject project, WebPreferences pref) {
		JarPackageData data = new JarPackageData();
		data.setCompress(false);

		IPath distPath = project.getLocation();
		String ctx = new Path(pref.getContextName()).removeTrailingSeparator()
				.toOSString();
		if (StringUtil.isEmpty(ctx)) {
			distPath = distPath.append(project.getName());
		} else {
			distPath = distPath.append(ctx);
		}
		distPath = distPath.addFileExtension("war");
		data.setJarLocation(distPath);
		return data;
	}

}