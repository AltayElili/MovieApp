package com.example.movie.presentation.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.movie.db.BaseFragment
import com.example.movie.databinding.FragmentDetailBinding
import com.example.movie.data.model.local.ListedContent
import com.example.movie.data.model.local.Movie
import com.example.movie.presentation.ui.list.ListViewModel
import com.example.movie.utils.gone
import com.example.movie.utils.visible
import com.google.android.material.tabs.TabLayoutMediator
import com.shashank.sony.fancytoastlib.FancyToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : BaseFragment<FragmentDetailBinding>(FragmentDetailBinding::inflate) {

    private val args: DetailFragmentArgs by navArgs()
    private val detailViewModel by viewModels<DetailViewModel>()
    private val listViewModel by activityViewModels<ListViewModel>()
    private val actorsAdapter = ActorsAdapter()
    private var isButtonChecked: Boolean = false
    private val detailPagerAdapter by lazy { DetailPagerAdapter(this, args.id, args.isMovie) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapters()
        observeData()
        tabMediator()
        getData()
        setupClicks()
        detailViewModel.isContentInList(args.id)
    }

    private fun tabMediator() {
        TabLayoutMediator(binding.tabLayout, binding.tabsViewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "More Like This"
                1 -> tab.text = "Comments"
            }
        }.attach()
    }


    private fun observeData() {
        detailViewModel.movieDetailState.observe(viewLifecycleOwner) {
            binding.animationView.gone()
            when (it) {
                is DetailViewModel.MovieDetailUiState.DetailLoading -> binding.animationView.visible()
                is DetailViewModel.MovieDetailUiState.DetailError -> showFancyToast(
                    it.message,
                    FancyToast.ERROR
                )

                is DetailViewModel.MovieDetailUiState.DetailSuccess -> {
                    val movieDetails = it.movieDetail
                    binding.movie = movieDetails
                    addContentToList(
                        movieDetails.voteAverage,
                        movieDetails.posterPath,
                        movieDetails.originalTitle,
                        movieDetails.id
                    )
                }
            }
        }
        detailViewModel.tvDetailState.observe(viewLifecycleOwner) {
            binding.animationView.gone()
            when (it) {
                is DetailViewModel.TvDetailUiState.TvDetailLoading -> binding.animationView.visible()
                is DetailViewModel.TvDetailUiState.TvDetailError -> showFancyToast(
                    it.message,
                    FancyToast.ERROR
                )

                is DetailViewModel.TvDetailUiState.TvDetailSuccess -> {
                    val tvSeriesDetails = it.tvDetail
                    binding.tvSeries = tvSeriesDetails
                    addContentToList(
                        tvSeriesDetails.voteAverage,
                        tvSeriesDetails.posterPath,
                        tvSeriesDetails.name,
                        tvSeriesDetails.id
                    )

                }
            }
        }
        detailViewModel.movieActorState.observe(viewLifecycleOwner) {
            binding.animationViewActors.gone()
            when (it) {
                is DetailViewModel.ActorUiState.ActorLoading -> binding.animationViewActors.visible()
                is DetailViewModel.ActorUiState.ActorError -> showFancyToast(
                    it.message,
                    FancyToast.ERROR
                )

                is DetailViewModel.ActorUiState.ActorSuccess -> {
                    val movieActors = it.detail.cast
                    if (movieActors.isNullOrEmpty()) {
                        binding.textViewActorsErrorMessage.visible()
                    } else actorsAdapter.submitList(movieActors)
                }
            }
        }
        detailViewModel.tvActorState.observe(viewLifecycleOwner) {
            binding.animationViewActors.gone()
            when (it) {
                is DetailViewModel.ActorUiState.ActorLoading -> binding.animationViewActors.visible()
                is DetailViewModel.ActorUiState.ActorError -> showFancyToast(
                    it.message,
                    FancyToast.ERROR
                )

                is DetailViewModel.ActorUiState.ActorSuccess -> {
                    val tvActors = it.detail.cast
                    if (tvActors.isNullOrEmpty()) {
                        binding.textViewActorsErrorMessage.visible()
                    } else actorsAdapter.submitList(tvActors)
                }
            }
        }
        detailViewModel.isContentInList.observe(viewLifecycleOwner) {
            if (it == 1) {
                isButtonChecked = true
                binding.buttonAddToList.isChecked = isButtonChecked
            } else if (it == 0) {
                isButtonChecked = false
                binding.buttonAddToList.isChecked = isButtonChecked
            }
        }
        binding.imageView9.setOnClickListener {
            val shareText = if (args.isMovie) {
                "Check out this movie: ${binding.movie?.originalTitle}\n\nhttps://www.themoviedb.org/movie/${args.id}"
            } else {
                "Check out this TV series: ${binding.tvSeries?.originalName}\n\nhttps://www.themoviedb.org/tv/${args.id}"
            }

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, shareText)
            startActivity(Intent.createChooser(intent, "Share via"))
        }

        binding.buttonDetailPlay.setOnClickListener {
            val youtubeUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl))
            intent.putExtra("force_fullscreen", true)
            startActivity(intent)
        }
    }

    private fun setupClicks() {
        binding.imageViewArrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setAdapters() {
        binding.rvActors.adapter = actorsAdapter
        binding.tabsViewPager.adapter = detailPagerAdapter
    }

    private fun setMovieDetails() {
        detailViewModel.getMovieDetails(args.id)
        detailViewModel.getMovieActors(args.id)
    }

    private fun setTvSeriesDetails() {
        detailViewModel.getTvSeriesDetails(args.id)
        detailViewModel.getTvActors(args.id)
    }

    private fun getData() {
        if (args.isMovie) {
            setMovieDetails()
        } else {
            setTvSeriesDetails()
        }
    }

    private fun addContentToList(
        voteAverage: Double?,
        image: String?,
        title: String?,
        id: Int?
    ) {
        binding.buttonAddToList.setOnClickListener {
            if (!isButtonChecked) {
                voteAverage?.let { rating ->
                    image?.let { poster ->
                        title?.let { title ->
                            id?.let { id ->
                                Movie(
                                    poster = poster,
                                    rating = rating,
                                    title = title,
                                    contentId = id.toString()
                                )
                            }
                        }
                    }
                }?.let {
                    listViewModel.addContentToList(it)
                    detailViewModel.addListedContentId(ListedContent(listedContentId = it.contentId))
                }
                isButtonChecked = true
                showFancyToast("Successfully added to the list!", FancyToast.SUCCESS)
            } else {
                voteAverage?.let { rating ->
                    image?.let { poster ->
                        title?.let { title ->
                            id?.let { id ->
                                Movie(
                                    poster = poster,
                                    rating = rating,
                                    title = title,
                                    contentId = id.toString()
                                )
                            }
                        }
                    }
                }?.let {
                    listViewModel.deleteContent(it)
                }
                isButtonChecked = false
                detailViewModel.deleteListedContentId(ListedContent(args.id, args.id.toInt()))
                showFancyToast("Successfully removed from the list!", FancyToast.SUCCESS)
            }

        }
    }

}
