package com.example.tugasakhir_145.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tugasakhir_145.R
import com.example.tugasakhir_145.databinding.FragmentProductListBinding
import com.example.tugasakhir_145.ui.adapter.ProductAdapter
import com.example.tugasakhir_145.ui.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

class ProductListFragment : Fragment() {

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ProductAdapter { product ->
            // On Click -> Edit
            // Pass ID via bundle, for now just simplistic
            // findNavController().navigate(ProductListFragmentDirections.actionToEdit(product.id))
            // But I haven't setup SafeArgs.
            
            val bundle = Bundle().apply {
                putInt("productId", product.id)
            }
            findNavController().navigate(R.id.action_productListFragment_to_addEditProductFragment, bundle)
        }

        binding.rvProducts.layoutManager = GridLayoutManager(context, 2)
        binding.rvProducts.adapter = adapter

        lifecycleScope.launch {
            viewModel.allProducts.collect { products ->
                adapter.submitList(products)
            }
        }

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_productListFragment_to_addEditProductFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
