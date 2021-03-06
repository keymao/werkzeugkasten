package com.google.werkzeugkasten.core.web;

import com.google.werkzeugkasten._;

public interface Renderer<APP, REQ, RES> {

	<CTX extends WebContext<APP, REQ, RES, _>> void render(CTX context);
}
