package com.example.cf.tutorialsondemand.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.example.cf.tutorialsondemand.R
import kotlinx.android.synthetic.main.fragment_select_action.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "someTitle"
private const val ARG_PARAM2 = "somePage"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SelectActionFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SelectActionFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SelectActionFragment : Fragment() {

    private var title: String = " "
    private var page: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_PARAM2, title)
            page = it.getInt(ARG_PARAM1, page)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_select_action, container, false)
        view.findViewById<TextView>(R.id.txtView).text = "This is $title -- Page $page"

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SelectActionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: Int, param2: String) =
                SelectActionFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
