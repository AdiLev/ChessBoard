package com.chessboard.app

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding

class PromotionDialog(
    context: Context,
    private val capturedPieces: List<ChessPiece>,
    private val pieceColor: PieceColor,
    private val onPieceSelected: (PieceType) -> Unit,
    private val onCancel: () -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_promotion)
        
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        
        setupDialog()
    }

    private fun setupDialog() {
        val titleText = findViewById<TextView>(R.id.titleText)
        val subtitleText = findViewById<TextView>(R.id.subtitleText)
        val piecesContainer = findViewById<LinearLayout>(R.id.piecesContainer)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        titleText.text = "Pawn Promotion"
        subtitleText.text = "Select a captured piece type to promote your pawn"
        subtitleText.setTextColor(Color.parseColor("#FFCCCCCC"))
        
        // Show unique piece types from captured pieces
        // Each type can be selected multiple times for different pawn promotions
        val availableTypes = capturedPieces.map { it.type }.distinct()
        availableTypes.forEachIndexed { index, pieceType ->
            val piece = ChessPiece(pieceType, pieceColor)
            val pieceView = createPieceView(piece, index)
            piecesContainer.addView(pieceView)
        }

        btnCancel.setOnClickListener {
            onCancel()
            dismiss()
        }
    }

    private fun createPieceView(piece: ChessPiece, index: Int): View {
        // Create a custom view that looks like a chess square with piece (like on the board)
        val container = LinearLayout(context)
        container.orientation = LinearLayout.VERTICAL
        container.gravity = Gravity.CENTER
        
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(12, 8, 12, 8)
        container.layoutParams = layoutParams
        
        // Create square background (like chess board) - alternate colors
        val squareView = View(context)
        val squareSize = 120 // pixels
        val squareParams = LinearLayout.LayoutParams(squareSize, squareSize)
        squareView.layoutParams = squareParams
        
        // Alternate between light and dark squares for visual appeal
        val isLight = (index % 2 == 0)
        squareView.setBackgroundColor(
            if (isLight) Color.parseColor("#F0D9B5") 
            else Color.parseColor("#B58863")
        )
        
        // Create piece text view (styled like on the board)
        val pieceText = TextView(context)
        pieceText.text = piece.getUnicodeSymbol()
        pieceText.textSize = 48f
        pieceText.gravity = Gravity.CENTER
        pieceText.setTextColor(if (piece.color == PieceColor.WHITE) Color.WHITE else Color.BLACK)
        
        // Add text view on top of square using FrameLayout
        val frameLayout = android.widget.FrameLayout(context)
        frameLayout.layoutParams = LinearLayout.LayoutParams(squareSize, squareSize)
        frameLayout.addView(squareView)
        frameLayout.addView(pieceText)
        
        // Make clickable with ripple effect
        frameLayout.isClickable = true
        frameLayout.isFocusable = true
        frameLayout.setOnClickListener {
            onPieceSelected(piece.type)
            dismiss()
        }
        
        // Add border for better visual separation
        frameLayout.setBackgroundResource(android.R.drawable.dialog_holo_light_frame)
        
        container.addView(frameLayout)
        
        // Add piece type label below
        val labelText = TextView(context)
        labelText.text = piece.type.name.lowercase().replaceFirstChar { it.uppercase() }
        labelText.textSize = 12f
        labelText.setTextColor(Color.parseColor("#FFCCCCCC"))
        labelText.gravity = Gravity.CENTER
        labelText.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 4, 0, 0)
        }
        container.addView(labelText)
        
        return container
    }
}

