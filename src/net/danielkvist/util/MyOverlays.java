package net.danielkvist.util;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * This class adds a marker to an Overlay that can be used to mark positions on a MapView
 * 
 * @author Daniel Kvist
 * 
 */
public class MyOverlays extends ItemizedOverlay
{
    private List<OverlayItem> overlays = new ArrayList<OverlayItem>();

    /**
     * Constructor which uses a Drawable as a default marker
     * 
     * @param defaultMarker
     *            the default marker
     */
    public MyOverlays(Drawable defaultMarker)
    {
        super(boundCenterBottom(defaultMarker));
    }

    /**
     * Adds the item to the overlays list
     * 
     * @param overlay
     *            the item to add
     */
    public void addOverlay(OverlayItem overlay)
    {
        overlays.add(overlay);
        populate();
    }

    /**
     * Returns the item at the corresponding position to i
     */
    @Override
    protected OverlayItem createItem(int i)
    {
        return overlays.get(i);
    }

    /**
     * Returns the size of the overlay list
     */
    @Override
    public int size()
    {
        return overlays.size();
    }

}
