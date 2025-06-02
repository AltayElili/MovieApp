package com.example.movie.presentation.ui.detail

import android.Manifest
import com.example.movie.R
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.movie.db.BaseFragment
import com.example.movie.databinding.FragmentDetailBinding
import com.example.movie.data.model.local.ListedContent
import com.example.movie.data.model.local.Movie
import com.example.movie.presentation.ui.list.ListViewModel
import com.example.movie.utils.DownloadHelper
import com.example.movie.utils.gone
import com.example.movie.utils.visible
import com.google.android.material.tabs.TabLayoutMediator
import com.shashank.sony.fancytoastlib.FancyToast
import dagger.hilt.android.AndroidEntryPoint
import android.graphics.RenderEffect
import android.graphics.Shader
import android.view.WindowManager
import android.widget.ImageView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior

@AndroidEntryPoint
class DetailFragment : BaseFragment<FragmentDetailBinding>(FragmentDetailBinding::inflate) {

    private val args: DetailFragmentArgs by navArgs()
    private val detailViewModel by viewModels<DetailViewModel>()
    private val listViewModel by activityViewModels<ListViewModel>()
    private val actorsAdapter = ActorsAdapter()
    private var isButtonChecked: Boolean = false
    private val detailPagerAdapter by lazy { DetailPagerAdapter(this, args.id, args.isMovie) }
    private val SAMPLE_VIDEO =
        "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/720/Big_Buck_Bunny_720_10s_1MB.mp4"
    private val PERM_REQ_CODE = 601

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapters()
        observeData()
        tabMediator()
        getData()
        setupClicks()
        detailViewModel.isContentInList(args.id)
        binding.buttonDetailDownload.setOnClickListener {
            checkPermissionAndDownload()
        }
        binding.imageView17.setOnClickListener { showCastBottomSheet() }
    }

    private fun showCastBottomSheet() {
        val dialog = BottomSheetDialog(requireContext(), com.google.android.material.R.style.Animation_Material3_BottomSheetDialog)
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_cast, null)
        dialog.setContentView(sheetView)
        sheetView.post {
            (sheetView.parent as? View)?.let { sheet ->
                BottomSheetBehavior.from(sheet).apply {
                    peekHeight = (resources.displayMetrics.heightPixels * 0.55).toInt()
                    isFitToContents = false
                    state = BottomSheetBehavior.STATE_COLLAPSED
                }
                sheet.setBackgroundResource(R.drawable.check_off)
            }
        }
        dialog.window?.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setDimAmount(0f)
        }
        blurBackground(true)

        sheetView.findViewById<ImageView>(R.id.imageClose)?.setOnClickListener { dialog.dismiss() }

        dialog.setOnDismissListener { blurBackground(false) }
        dialog.show()
    }

    private fun blurBackground(enable: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            binding.root.setRenderEffect(
                if (enable) RenderEffect.createBlurEffect(25f, 25f, Shader.TileMode.CLAMP) else null
            )
        }
        binding.viewBlurOverlay.visibility = if (enable) View.VISIBLE else View.GONE
    }

    private fun tabMediator() {
        TabLayoutMediator(binding.tabLayout, binding.tabsViewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "More Like This"
                1 -> tab.text = "Comments"
            }
        }.attach()
    }


    private fun checkPermissionAndDownload() {
        val permission = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                Manifest.permission.READ_MEDIA_VIDEO

            else -> Manifest.permission.READ_EXTERNAL_STORAGE
        }

        val granted = ContextCompat.checkSelfPermission(
            requireContext(), permission
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            DownloadHelper.startDownload(requireContext(), SAMPLE_VIDEO)
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), PERM_REQ_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERM_REQ_CODE && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            DownloadHelper.startDownload(requireContext(), SAMPLE_VIDEO)
        } else if (requestCode == PERM_REQ_CODE) {
            showFancyToast("Permission denied – cannot download.", FancyToast.WARNING)
        }
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