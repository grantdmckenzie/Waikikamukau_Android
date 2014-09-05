package edu.ucsb.waikikamukau_app;

import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

public class MapBoxOnlineTileProvider extends UrlTileProvider {

    private static final String FORMAT;

    static {
        FORMAT = "http://api.tiles.mapbox.com/v3/%s/%d/%d/%d.png";
    }

    // ------------------------------------------------------------------------
    // Instance Variables
    // ------------------------------------------------------------------------

    private String mMapIdentifier;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    public MapBoxOnlineTileProvider(String mapIdentifier) {
        super(256, 256);

        this.mMapIdentifier = mapIdentifier;
    }

    // ------------------------------------------------------------------------
    // Public Methods
    // ------------------------------------------------------------------------

    @Override
    public URL getTileUrl(int x, int y, int z) {
        try {
            return new URL(String.format(FORMAT, this.mMapIdentifier, z, x, y));
        }
        catch (MalformedURLException e) {
            return null;
        }
    }

}