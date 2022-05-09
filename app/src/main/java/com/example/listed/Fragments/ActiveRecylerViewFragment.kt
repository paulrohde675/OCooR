package com.example.ocoor.Fragments

import android.R
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ocoor.Adapter.InactiveItemAdapter
import com.example.ocoor.Adapter.ItemAdapter
import com.example.ocoor.MainActivity
import com.example.ocoor.Utils.Item
import com.example.ocoor.databinding.ActiveRecyclerViewFragmentBinding
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round


class ActiveRecyclerViewFragment : Fragment() {

    // binding
    private lateinit var binding: ActiveRecyclerViewFragmentBinding

    lateinit var itemRecyclerView: RecyclerView;
    lateinit var inactiveItemRecyclerView: RecyclerView;
    lateinit var itemAdapter:ItemAdapter
    lateinit var inactiveItemAdapter:InactiveItemAdapter

    // main activity
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = requireActivity() as MainActivity

        binding = ActiveRecyclerViewFragmentBinding.inflate(layoutInflater)

        itemRecyclerView = binding.activeRecyclerView
        itemAdapter = ItemAdapter(mutableListOf(), mainActivity.mItemViewModel, mainActivity)
        itemRecyclerView.layoutManager = LinearLayoutManager(context)
        itemRecyclerView.adapter = itemAdapter

        inactiveItemRecyclerView = binding.inactiveRecyclerView
        inactiveItemAdapter = InactiveItemAdapter(mutableListOf(), mainActivity.mItemViewModel, mainActivity)
        inactiveItemRecyclerView.layoutManager = LinearLayoutManager(context)
        inactiveItemRecyclerView.adapter = inactiveItemAdapter


        binding.seperatorLineView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                var height = 0

                when (event!!.action) {
                    MotionEvent.ACTION_DOWN -> {
                        return true
                    }

                    MotionEvent.ACTION_UP ->
                    {

                    }
                    MotionEvent.ACTION_MOVE ->
                    {

                        val size = Point()
                        mainActivity.windowManager.defaultDisplay.getSize(size)
                        height = size.y

                        val guideLine = binding.seperatorLine
                        val params = guideLine.layoutParams as ConstraintLayout.LayoutParams
                        val percent = min(max(event.getRawY() / height, 0.05f), 0.9f)

                        mainActivity.splitRatio = percent
                        params.guidePercent = percent
                        guideLine.layoutParams = params
                        return true
                    }
                }
                return false
            }
        })

        return binding.root
    }

    fun convertPixelsToDp(px: Float, context: Context): Float {
        return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }



    override fun onResume() {
        super.onResume()

        // For some reasons it is necessary to add an item, otherwise the recycler view is empty.
        val item = Item(id=9999999, list_id = 9999999)
        mainActivity.mItemViewModel.addItem(item)
        mainActivity.mItemViewModel.rmItem(item)
    }

    override fun onStart() {
        super.onStart()
    }
}