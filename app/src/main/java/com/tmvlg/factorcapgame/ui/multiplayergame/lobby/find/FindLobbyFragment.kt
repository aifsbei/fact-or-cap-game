package com.tmvlg.factorcapgame.ui.multiplayergame.lobby.find

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tmvlg.factorcapgame.FactOrCapApplication
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.data.repository.firebase.Lobby
import com.tmvlg.factorcapgame.data.repository.firebase.Player
import com.tmvlg.factorcapgame.databinding.FragmentFindLobbyBinding
import com.tmvlg.factorcapgame.databinding.LobbyBinding
import com.tmvlg.factorcapgame.ui.menu.MenuFragment
import com.tmvlg.factorcapgame.ui.multiplayergame.lobby.LobbyFragment
import com.tmvlg.factorcapgame.ui.multiplayergame.lobby.find.outdated.LobbiesListAdapter
import mva3.adapter.ListSection
import mva3.adapter.MultiViewAdapter
import mva3.adapter.util.Mode

class FindLobbyFragment : Fragment() {

    private val viewModel: FindLobbyViewModel by viewModels {
        // inits viewmodel
        val app = activity?.application as FactOrCapApplication
        return@viewModels FindLobbyViewModelFactory(
            app.firebaseRepository,
            app.userRepository
        )
    }

    private var _binding: FragmentFindLobbyBinding? = null

    private val binding: FragmentFindLobbyBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")

    private var isEnabled: Boolean
        get() = binding.root.isEnabled
        set(value) {
            binding.root.isEnabled = value
        }

    private val lobbyListSection = ListSection<Lobby>()
    private val lobbyListAdapter = LobbyListAdapter(
        object : LobbyBinder.OnLobbySelectedListener {
            override fun onLobbySelected(binding: LobbyBinding, isSelected: Boolean) {
                with(binding.lobbyItem) {
                    Log.d("FindLobby", "OnSelect: $isSelected")
                    background = if (isSelected) {
                        AppCompatResources.getDrawable(context, R.color.primary_red)
                    } else {
                        AppCompatResources.getDrawable(context, R.color.transparent)
                    }
                }
            }

        }
    )

    init {
        lobbyListSection.setSelectionMode(Mode.SINGLE)
        lobbyListAdapter.addSection(lobbyListSection)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.listenLobbies()
//        loadState(requireArguments())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFindLobbyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.returnButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, MenuFragment())
                .commit()
        }
        binding.joinButton.setOnClickListener {
            isEnabled = false
            if (lobbyListSection.selectedItems.isEmpty()) {
                Toast.makeText(context, "Select lobby!", Toast.LENGTH_SHORT).show()
                isEnabled = true
                return@setOnClickListener
            }

            viewModel.connectLobby(lobbyListSection.selectedItems)

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(
                    R.id.main_fragment_container,
                    LobbyFragment.newInstance(
                        lobbyListSection.selectedItems.first(),
                        Player.Type.PLAYER
                    )
                )
                .commit()
        }
//        if (savedInstanceState != null) {
//            loadState(savedInstanceState)
//        }
        binding.findLobbyRecyclerview.adapter = lobbyListAdapter
        binding.findLobbyRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.findLobbyRecyclerview.addItemDecoration(lobbyListAdapter.itemDecoration)
        observeViewModel()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        viewModel.stopListenLobbies()
        super.onDestroy()
    }

    private fun observeViewModel() {
        viewModel.lobbies.observe(viewLifecycleOwner) { lobbies ->
            Log.d(tag, "lobbies in fragment: $lobbies")
            lobbyListSection.set(lobbies)
        }
        viewModel.connectedLobby.observe(viewLifecycleOwner) { lobby ->
            if (lobby == null) {
                Toast.makeText(context, "Select lobby!", Toast.LENGTH_SHORT).show()
                return@observe
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(
                    R.id.main_fragment_container,
                    LobbyFragment.newInstance(lobby, Player.Type.PLAYER)
                )
                .commit()
        }
    }

    companion object {
        fun newInstance(): FindLobbyFragment {
            return FindLobbyFragment()
        }
    }
}