package com.chessboard.app

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
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

        // Get unique piece types from captured pieces
        val availableTypes = capturedPieces.map { it.type }.distinct()
        
        if (availableTypes.isEmpty()) {
            subtitleText.text = "No captured pieces available. Using standard promotion options."
            subtitleText.setTextColor(Color.parseColor("#FFFFD93D"))
            // Show standard promotion options (queen, rook, bishop, knight)
            listOf(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT).forEach { pieceType ->
                val button = createPieceButton(pieceType)
                piecesContainer.addView(button)
            }
        } else {
            subtitleText.text = "Select a piece from your captured pieces"
            // Create buttons for each available piece type
            availableTypes.forEach { pieceType ->
                val button = createPieceButton(pieceType)
                piecesContainer.addView(button)
            }
        }

        btnCancel.setOnClickListener {
            onCancel()
            dismiss()
        }
    }

    private fun createPieceButton(pieceType: PieceType): Button {
        val button = Button(context)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        button.layoutParams = layoutParams
        
        // Create a temporary piece to get the unicode symbol
        val tempPiece = ChessPiece(pieceType, pieceColor)
        button.text = tempPiece.getUnicodeSymbol()
        button.textSize = 32f
        button.setPadding(24)
        button.minWidth = 120
        button.minHeight = 120
        
        button.setOnClickListener {
            onPieceSelected(pieceType)
            dismiss()
        }
        
        return button
    }
}

