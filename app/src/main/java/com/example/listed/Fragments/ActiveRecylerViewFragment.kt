package com.example.ocoor.Fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ocoor.Adapter.InactiveItemAdapter
import com.example.ocoor.Adapter.ItemAdapter
import com.example.ocoor.MainActivity
import com.example.ocoor.Utils.Item
import com.example.ocoor.databinding.ActiveRecyclerViewFragmentBinding

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

        /*
        binding.slider.setOnDragListener { view, dragEvent ->

            //2
            val draggableItem = dragEvent.localState as View

            println("Drag")

            //3
            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    true
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    view.invalidate()
                    true
                }
                DragEvent.ACTION_DRAG_LOCATION -> {
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    true
                }
                DragEvent.ACTION_DROP -> {
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    true
                }
                else -> {
                    false
                }
            }
        }
        */

        var dX = 0f
        var dY = 0f

        binding.slider.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {


                when (event!!.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // apply the change to the dimensions of your view;
                        // for example with animation and using scale parameters, like this:
                        v!!.animate().scaleX(0.6f).scaleY(0.6f)

                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();

                        return true
                    }
                    MotionEvent.ACTION_CANCEL,

                    MotionEvent.ACTION_UP ->                 // revert to the normal dimensions of your view
                        // for example with animation and using scale parameters, like this:

                    {
                        v!!.animate().scaleX(1f).scaleY(1f)
                        v!!.animate().x(event.getRawX() + dX).x(event.getRawX() + dX).y(event.getRawY() + dY).setDuration(0).start()

                    }




                }
                return false
            }
        })


        /*
        myOnTouchListener = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent me){
                if (me.getAction() == MotionEvent.ACTION_DOWN){
                    oldXvalue = me.getX();
                    oldYvalue = me.getY();
                    Log.i(myTag, "Action Down " + oldXvalue + "," + oldYvalue);
                }else if (me.getAction() == MotionEvent.ACTION_MOVE  ){
                    LayoutParams params = new LayoutParams(v.getWidth(), v.getHeight(),(int)(me.getRawX() - (v.getWidth() / 2)), (int)(me.getRawY() - (v.getHeight())));
                    v.setLayoutParams(params);
                }
                return true;
            }
         */
        return binding.root
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