package com.chessboard.app

import android.media.ToneGenerator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var chessBoardView: ChessBoardView
    private lateinit var moveSeekBar: SeekBar
    private lateinit var moveNumberText: TextView
    private lateinit var totalMovesText: TextView
    private lateinit var moveHistoryText: TextView
    private lateinit var btnPauseResume: Button
    private lateinit var btnFirst: Button
    private lateinit var btnPrevious: Button
    private lateinit var btnNext: Button
    private lateinit var btnLast: Button
    private lateinit var btnNewGame: Button
    private lateinit var btnFullscreen: Button
    private lateinit var boardContainer: android.widget.FrameLayout
    private lateinit var controlsContainer: android.widget.LinearLayout

    private val game = ChessGame()
    private val handler = Handler(Looper.getMainLooper())
    private var autoPlayRunnable: Runnable? = null
    private var isSeeking = false
    private var isFullscreen = false
    private val toneGenerator = ToneGenerator(android.media.AudioManager.STREAM_MUSIC, 100)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupChessBoard()
        setupControls()
        updateUI()
        
        // Initialize autoplay as paused (showing play icon)
        game.pause()
    }

    private fun initializeViews() {
        chessBoardView = findViewById(R.id.chessBoardView)
        moveSeekBar = findViewById(R.id.moveSeekBar)
        moveNumberText = findViewById(R.id.moveNumberText)
        totalMovesText = findViewById(R.id.totalMovesText)
        moveHistoryText = findViewById(R.id.moveHistoryText)
        btnPauseResume = findViewById(R.id.btnPauseResume)
        btnFirst = findViewById(R.id.btnFirst)
        btnPrevious = findViewById(R.id.btnPrevious)
        btnNext = findViewById(R.id.btnNext)
        btnLast = findViewById(R.id.btnLast)
        btnNewGame = findViewById(R.id.btnNewGame)
        btnFullscreen = findViewById(R.id.btnFullscreen)
        boardContainer = findViewById(R.id.boardContainer)
        controlsContainer = findViewById(R.id.controlsContainer)
        
        // Ensure all buttons are enabled and clickable
        btnNewGame.isEnabled = true
        btnFirst.isEnabled = true
        btnPrevious.isEnabled = true
        btnPauseResume.isEnabled = true
        btnNext.isEnabled = true
        btnLast.isEnabled = true
        btnFullscreen.isEnabled = true
        chessBoardView.isEnabled = true
    }

    private fun setupChessBoard() {
        chessBoardView.setGame(game)
        chessBoardView.onMoveListener = { from, to ->
            when (val result = game.makeMove(from, to)) {
                is MoveResult.Success -> {
                    // Clear selection after successful move
                    chessBoardView.clearSelection()
                    updateUI()
                }
                is MoveResult.PromotionRequired -> {
                    // Keep selection for promotion
                    showPromotionDialog(result.from, result.to, result.piece)
                }
                is MoveResult.Invalid -> {
                    // Play beep sound for illegal move
                    toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 200)
                    // Keep selection so user can try another move
                }
            }
        }
    }
    
    private fun showPromotionDialog(from: Square, to: Square, piece: ChessPiece) {
        val capturedPieces = game.getCapturedPieces(piece.color)
        
        // If no captured pieces, show standard promotion options as fallback
        val piecesToShow = if (capturedPieces.isEmpty()) {
            listOf(
                ChessPiece(PieceType.QUEEN, piece.color),
                ChessPiece(PieceType.ROOK, piece.color),
                ChessPiece(PieceType.BISHOP, piece.color),
                ChessPiece(PieceType.KNIGHT, piece.color)
            )
        } else {
            capturedPieces
        }
        
        val dialog = PromotionDialog(
            this,
            piecesToShow,
            piece.color,
            onPieceSelected = { promotionType ->
                // Complete the promotion
                when (val result = game.completePromotion(from, to, promotionType)) {
                    is MoveResult.Success -> {
                        // Clear selection after successful promotion
                        chessBoardView.clearSelection()
                        updateUI()
                    }
                    else -> {
                        // Handle error - promotion failed
                        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 200)
                    }
                }
            },
            onCancel = {
                // User cancelled - the pawn move is not completed, so nothing happens
                // The pawn remains in its original position
            }
        )
        dialog.show()
    }

    private fun setupControls() {
        // SeekBar for timeline navigation
        moveSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && !isSeeking) {
                    isSeeking = true
                    val targetIndex = progress - 1 // -1 for initial position
                    game.goToMove(targetIndex)
                    updateUI()
                    isSeeking = false
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                pauseAutoPlay()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Navigation buttons
        btnFirst.setOnClickListener {
            pauseAutoPlay()
            chessBoardView.clearSelection()
            game.goToFirstMove()
            updateUI()
        }

        btnPrevious.setOnClickListener {
            pauseAutoPlay()
            chessBoardView.clearSelection()
            game.goToPreviousMove()
            updateUI()
        }

        btnNext.setOnClickListener {
            pauseAutoPlay()
            chessBoardView.clearSelection()
            game.goToNextMove()
            updateUI()
        }

        btnLast.setOnClickListener {
            pauseAutoPlay()
            chessBoardView.clearSelection()
            game.goToLastMove()
            updateUI()
        }

        // Pause/Resume button
        btnPauseResume.setOnClickListener {
            if (game.isPaused()) {
                resumeAutoPlay()
            } else {
                pauseAutoPlay()
            }
        }

        // New Game button
        btnNewGame.setOnClickListener {
            pauseAutoPlay()
            chessBoardView.clearSelection()
            game.newGame()
            updateUI()
        }
        
        // Fullscreen button
        btnFullscreen.setOnClickListener {
            toggleFullscreen()
        }
    }
    
    private fun toggleFullscreen() {
        isFullscreen = !isFullscreen
        
        if (isFullscreen) {
            // Enter fullscreen: hide controls and action bar, show only board
            controlsContainer.visibility = android.view.View.GONE
            supportActionBar?.hide() // Hide action bar in fullscreen
            btnFullscreen.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.ic_fullscreen_exit,
                0,
                0
            )
            btnFullscreen.contentDescription = "Exit fullscreen"
            // Make board container fill entire screen
            boardContainer.layoutParams.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT
            boardContainer.layoutParams = boardContainer.layoutParams
        } else {
            // Exit fullscreen: show controls and action bar, restore normal layout
            controlsContainer.visibility = android.view.View.VISIBLE
            supportActionBar?.show() // Show action bar when exiting fullscreen
            btnFullscreen.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.ic_fullscreen,
                0,
                0
            )
            btnFullscreen.contentDescription = "Fullscreen"
            // Restore board container to weighted layout
            val params = boardContainer.layoutParams as android.widget.LinearLayout.LayoutParams
            params.height = 0
            params.weight = 1f
            boardContainer.layoutParams = params
        }
    }

    private fun updateUI() {
        // Update chess board
        chessBoardView.invalidate()

        // Update move counter and seek bar
        val currentIndex = game.getCurrentMoveIndex()
        val totalMoves = game.getTotalMoves()
        
        moveNumberText.text = "Move: ${currentIndex + 1}"
        totalMovesText.text = "/ $totalMoves"

        // Update seek bar (add 1 for initial position)
        moveSeekBar.max = totalMoves.coerceAtLeast(0)
        if (!isSeeking) {
            moveSeekBar.progress = currentIndex + 1
        }

        // Update move history text
        val history = game.getMoveHistory()
        val historyText = if (history.isEmpty()) {
            "Move History: (No moves yet)"
        } else {
            val displayedMoves = history.takeLast(10)
            "Move History: ${displayedMoves.joinToString(" ") { it.toNotation() }}"
        }
        moveHistoryText.text = historyText

        // Update pause/resume button icon
        btnPauseResume.setCompoundDrawablesWithIntrinsicBounds(
            0,
            if (game.isPaused()) R.drawable.ic_play else R.drawable.ic_pause,
            0,
            0
        )
    }

    private fun pauseAutoPlay() {
        game.pause()
        autoPlayRunnable?.let { handler.removeCallbacks(it) }
        autoPlayRunnable = null
        game.setAutoPlaying(false)
    }

    private fun resumeAutoPlay() {
        game.resume()
        game.setAutoPlaying(true)
        startAutoPlay()
    }

    private fun startAutoPlay() {
        autoPlayRunnable = object : Runnable {
            override fun run() {
                if (game.isAutoPlaying() && !game.isPaused()) {
                    val currentIndex = game.getCurrentMoveIndex()
                    val totalMoves = game.getTotalMoves()

                    if (currentIndex < totalMoves - 1) {
                        game.goToNextMove()
                        updateUI()
                        handler.postDelayed(this, 1000) // 1 second delay between moves
                    } else {
                        pauseAutoPlay()
                    }
                }
            }
        }
        handler.post(autoPlayRunnable!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        pauseAutoPlay()
        toneGenerator.release()
    }
}


