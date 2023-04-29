package com.epfl.drawyourpath.pathDrawing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.epfl.Utils.drawyourpath.Utils
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.google.android.gms.maps.model.LatLng

class PathDrawingPathPreviewFragment(private val run: Run) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_path_drawing_path_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val runCoordinates: List<LatLng> = run.getPath().getPoints() // Get the coordinates for this specific run
        val apiKey = "AIzaSyCE8covSYZE_sOv4Z-HaoljRlNOTV8cKRk"

        val staticMapUrl = Utils.getStaticMapUrl(runCoordinates, apiKey)

        // Load the image using Glide
        val imageView = view.findViewById<ImageView>(R.id.path_preview)
        Glide.with(imageView)
            .load(staticMapUrl)
            .placeholder(R.drawable.map_loading_placeholder) // Set a placeholder image while loading
            .into(imageView)
    }
}