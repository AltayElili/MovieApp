package com.example.movie.presentation.ui.payment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.movie.R
import com.example.movie.databinding.PaymentFragmentBinding

class PaymentFragment : Fragment() {

    private var _binding: PaymentFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PaymentFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.payButton.setOnClickListener {
            val cardNumber = binding.cardNumberEditText.text.toString()
            val expiryDate = binding.expiryDateEditText.text.toString()
            val cvv = binding.cvvEditText.text.toString()

            if (cardNumber.length == 16 && expiryDate.isNotEmpty() && cvv.length == 3) {
                Toast.makeText(requireContext(), "Payment successful.", Toast.LENGTH_LONG).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    findNavController().navigate(R.id.action_paymentFragment_to_homeFragment)
                }, 2000)
            } else {
                Toast.makeText(requireContext(), "Please enter valid payment details.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
