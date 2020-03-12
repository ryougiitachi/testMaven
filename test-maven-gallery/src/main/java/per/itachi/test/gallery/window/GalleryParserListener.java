package per.itachi.test.gallery.window;

import java.util.EventObject;

/**
 * Coupling degree is a little high. 
 * */
public interface GalleryParserListener {
	
	void galleryStateChanged(EventObject event);
}
