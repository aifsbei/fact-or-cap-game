package com.tmvlg.factorcapgame.ui.multiplayergame.lobby.find

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tmvlg.factorcapgame.data.repository.firebase.Lobby
import com.tmvlg.factorcapgame.databinding.LobbyBinding

class LobbyListAdapter(
    var onSelectedListener: OnSelectedListener? = null
) : ListAdapter<Lobby, LobbyListAdapter.LobbyViewHolder>(LobbyDiffCallback()) {
    var selectedPosition: Int? = null
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LobbyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LobbyBinding.inflate(inflater, parent, false)
        return LobbyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LobbyViewHolder, position: Int) {
        val lobby = getItem(position)
        with(holder) {
            binding.lobby.text = lobby.name
            binding.host.text = lobby.hostName
            binding.playersNumber.text = lobby.players.size.toString()
            binding.root.setOnClickListener { v ->
                selectedPosition = if (selectedPosition == position) {
                    null
                } else {
                    position
                }
                onSelectedListener?.onSelected(binding, selectedPosition == position)
            }
        }
    }

    class LobbyViewHolder(
        val binding: LobbyBinding
    ) : RecyclerView.ViewHolder(binding.root)

    class LobbyDiffCallback : DiffUtil.ItemCallback<Lobby>() {
        override fun areItemsTheSame(oldItem: Lobby, newItem: Lobby): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Lobby, newItem: Lobby): Boolean {
            return oldItem.name == newItem.name
                    && oldItem.hostName == newItem.hostName
                    && oldItem.players.size == oldItem.players.size
        }
    }

    interface OnSelectedListener {
        fun onSelected(binding: LobbyBinding, isSelected: Boolean)
    }
}
