package edu.ucsb.waikikamukau_app;

import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;

class Glistener implements OnItemGestureListener<OverlayItem> {
     @Override
     public boolean onItemLongPress(int index, OverlayItem item) {
         //Toast.makeText(SampleWithMinimapItemizedoverlay.this, "Item " + item.mTitle,
         //        Toast.LENGTH_LONG).show();

         return false;
     }

     @Override
     public boolean onItemSingleTapUp(int index, OverlayItem item) {
        //Toast.makeText(SampleWithMinimapItemizedoverlay.this, "Item " + item.mTitle,
          //       Toast.LENGTH_LONG).show();
         return true; // We 'handled' this event.

     }

 }