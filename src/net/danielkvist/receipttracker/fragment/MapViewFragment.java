package net.danielkvist.receipttracker.fragment;

import net.danielkvist.receipttracker.R;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MapViewFragment extends Fragment {
	protected MapView mapView;
	protected GoogleMap map;
	protected int mapViewResourceId;
	protected int mapContainerLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(mapContainerLayout, container, false);

		// Gets the MapView from the XML layout and creates it
		mapView = (MapView) v.findViewById(mapViewResourceId);
		mapView.onCreate(savedInstanceState);
		mapView.setVisibility(View.GONE);

		return v;
	}

	public void enableMap() {
		// Gets to GoogleMap from the MapView and does initialization stuff
		map = mapView.getMap();
		map.getUiSettings().setMyLocationButtonEnabled(false);
		map.setMyLocationEnabled(true);

		MapsInitializer.initialize(this.getActivity());
		mapView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onResume() {
		mapView.onResume();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapView.onLowMemory();
	}
}