package per.itachi.test.gallery.window;

import java.util.EventObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import per.itachi.test.gallery.component.FrameOperationAdapter;
import per.itachi.test.gallery.entity.FrameGalleryItemEntity;

@Component
public class MainFrameGalleryParserListener implements GalleryParserListener, Runnable {
	
	private final Logger logger = LoggerFactory.getLogger(MainFrameGalleryParserListener.class);
	
	@Autowired
	private FrameOperationAdapter adapter;

	@Override
	public void run() {
		FrameGalleryItemEntity entity = null;
		try {
			do {
				entity = adapter.getUpdatedItem();
//				MainFrame.this.galleryStateChanged(new EventObject(entity));
			} 
			while (entity != null);
		} 
		catch (InterruptedException e) {
			logger.error("Exception occured when getting updated gallery item. ", e);
		}
	}

	@Override
	public void galleryStateChanged(EventObject event) {
	}
}
