package net.danielkvist.util;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MyOverlays extends ItemizedOverlay
{
    private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();

    public MyOverlays(Drawable defaultMarker)
    {
        super(boundCenterBottom(defaultMarker));
    }

    public void addOverlay(OverlayItem overlay)
    {
        overlays.add(overlay);
        populate();
    }

    @Override
    protected OverlayItem createItem(int i)
    {
        return overlays.get(i);
    }

    @Override
    public int size()
    {
        return overlays.size();
    }

}
