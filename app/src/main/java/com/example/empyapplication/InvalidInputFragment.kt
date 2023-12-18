package com.example.empyapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.FragmentContainerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InvalidInputFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InvalidInputFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val tmp = inflater.inflate(R.layout.fragment_invalid_input, container, false)

        val okayButton = tmp.findViewById<Button>(R.id.okay)
        okayButton.setOnClickListener {
//            Toast.makeText(context, "Clc", Toast.LENGTH_LONG).show()
//            requireActivity().finish()
//            getActivity()?.supportFragmentManager?.popBackStack()
//            val man = requireActivity().supportFragmentManager
////            man.beginTransaction().remove(this).commit();
//            activity?.onBackPressed()

//            this.activity?.findViewById<FragmentContainerView>(R.id.fragment_container_view)?.visibility =
//                View.GONE
        }

        return tmp
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InvalidInputFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InvalidInputFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}