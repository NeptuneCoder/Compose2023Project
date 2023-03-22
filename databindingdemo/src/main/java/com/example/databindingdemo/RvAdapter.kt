package com.example.databindingdemo

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.databindingdemo.databinding.RvItemViewBinding

class RvViewHolder(val binding: RvItemViewBinding) : RecyclerView.ViewHolder(binding.root)

class RvAdapter(val data: List<User>) : RecyclerView.Adapter<RvViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvViewHolder {
        val binding = DataBindingUtil.inflate<RvItemViewBinding>(
            LayoutInflater.from(parent.context),
            R.layout.rv_item_view,
            parent,
            false
        )
        Log.i("rvtag", "onCreateViewHolder == " + binding)
        return RvViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RvViewHolder, position: Int) {
        holder.binding.user = data[position]
        holder.binding.executePendingBindings()
        Log.i("rvtag", "onBindViewHolder == " + position)
    }
}