package com.project.cafehub.view.cafe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.cafehub.R
import com.project.cafehub.databinding.FragmentCafeInfoBinding
import com.project.cafehub.model.CafeWorkHours

class CafeInfoFragment : Fragment() {

    private var currentCafeWorkHours: CafeWorkHours?=null
    private var currentCafeAddress: String?=null
    private var binding: FragmentCafeInfoBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = arguments
        currentCafeWorkHours = bundle?.getParcelable<CafeWorkHours>("currentCafeWorkHours")
        currentCafeAddress = bundle?.getString("currentCafeAddress")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cafe_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCafeInfoBinding.bind(view)

        initCafeAddress()
        initCafeWorkHours()



    }

    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }

    private fun initCafeAddress(){
        binding!!.cafeAddress.text = currentCafeAddress
    }

    private fun initCafeWorkHours(){
        binding!!.sundayCafeWorkHour.text="Pazar: "+currentCafeWorkHours!!.sundayWorkHour
        binding!!.mondayCafeWorkHour.text="Pazartesi: "+currentCafeWorkHours!!.mondayWorkHour
        binding!!.tuesdayCafeWorkHour.text="Salı: "+currentCafeWorkHours!!.tuesdayWorkHour
        binding!!.wednesdayCafeWorkHour.text="Çarşamba: "+currentCafeWorkHours!!.wednesdayWorkHour
        binding!!.thursdayCafeWorkHour.text="Perşembe: "+currentCafeWorkHours!!.thursdayWorkHour
        binding!!.fridayCafeWorkHour.text="Cuma: "+currentCafeWorkHours!!.fridayWorkHour
        binding!!.saturdayCafeWorkHour.text="Cumartesi: "+currentCafeWorkHours!!.saturdayWorkHour
    }
}