package com.chessboard.app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat

class ChessBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var boardSize = 0
    private var squareSize = 0f
    private var selectedSquare: Square? = null
    private var possibleMoves = mutableListOf<Square>()
    private var game: ChessGame? = null

    private val lightSquarePaint = Paint().apply {
        color = Color.parseColor("#F0D9B5")
        style = Paint.Style.FILL
    }

    private val darkSquarePaint = Paint().apply {
        color = Color.parseColor("#B58863")
        style = Paint.Style.FILL
    }

    private val selectedSquarePaint = Paint().apply {
        color = Color.parseColor("#7FC3FF")
        style = Paint.Style.FILL
        alpha = 150
    }

    private val possibleMovePaint = Paint().apply {
        color = Color.parseColor("#7FC3FF")
        style = Paint.Style.FILL
        alpha = 100
    }

    private val piecePaint = Paint().apply {
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val borderPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    var onMoveListener: ((Square, Square) -> Unit)? = null

    fun setGame(game: ChessGame) {
        this.game = game
        invalidate()
    }

    fun setSelectedSquare(square: Square?) {
        selectedSquare = square
        invalidate()
    }

    fun setPossibleMoves(moves: List<Square>) {
        possibleMoves = moves.toMutableList()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = Math.min(measuredWidth, measuredHeight)
        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        boardSize = Math.min(w, h)
        squareSize = boardSize / 8f
        piecePaint.textSize = squareSize * 0.7f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw board squares
        for (row in 0..7) {
            for (col in 0..7) {
                val square = Square(row, col)
                val left = col * squareSize
                val top = row * squareSize
                val right = left + squareSize
                val bottom = top + squareSize

                val isLight = (row + col) % 2 == 0
                val paint = if (isLight) lightSquarePaint else darkSquarePaint

                // Draw selected square highlight
                if (selectedSquare?.row == row && selectedSquare?.col == col) {
                    canvas.drawRect(left, top, right, bottom, selectedSquarePaint)
                } else if (possibleMoves.any { it.row == row && it.col == col }) {
                    canvas.drawRect(left, top, right, bottom, possibleMovePaint)
                } else {
                    canvas.drawRect(left, top, right, bottom, paint)
                }

                // Draw border
                canvas.drawRect(left, top, right, bottom, borderPaint)

                // Draw piece
                val piece = game?.getPieceAt(row, col)
                if (piece != null) {
                    val x = left + squareSize / 2
                    val y = top + squareSize / 2 + piecePaint.textSize / 3
                    canvas.drawText(piece.getUnicodeSymbol(), x, y, piecePaint)
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val col = (event.x / squareSize).toInt()
            val row = (event.y / squareSize).toInt()

            if (row in 0..7 && col in 0..7) {
                val square = Square(row, col)

                if (selectedSquare == null) {
                    // Select a piece
                    val piece = game?.getPieceAt(row, col)
                    if (piece != null) {
                        selectedSquare = square
                        // Calculate possible moves (simplified - show all empty squares for now)
                        possibleMoves = (0..7).flatMap { r ->
                            (0..7).map { c -> Square(r, c) }
                        }.filter { game?.getPieceAt(it.row, it.col) == null }.toMutableList()
                        invalidate()
                    }
                } else {
                    // Try to make a move
                    if (selectedSquare != square) {
                        onMoveListener?.invoke(selectedSquare!!, square)
                    }
                    selectedSquare = null
                    possibleMoves.clear()
                    invalidate()
                }
            }
            return true
        }
        return super.onTouchEvent(event)
    }
}






