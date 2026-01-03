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

    init {
        isClickable = true
        isFocusable = true
        isEnabled = true
    }

    private var boardSize = 0
    private var squareSize = 0f
    private var selectedSquare: Square? = null
    private var possibleMoves = mutableListOf<Square>()
    private var castlingKingSquare: Square? = null // Highlight king when rook selected for castling
    private var game: ChessGame? = null
    
    // Callback for when castling should be confirmed (rook selected on edge)
    var onCastlingCheckListener: ((Square, ChessPiece, Boolean) -> Unit)? = null

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
        color = Color.BLACK // Default color, will be changed per piece
    }
    
    private val whitePiecePaint = Paint().apply {
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    
    private val whitePieceOutlinePaint = Paint().apply {
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        color = Color.parseColor("#808080") // Gray color for outlines/details
        style = Paint.Style.FILL
    }
    
    private val whitePieceDetailPaint = Paint().apply {
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        color = Color.parseColor("#808080") // Gray color for details
        style = Paint.Style.FILL
    }
    
    /**
     * Draws a white piece with gray outline/details and white fill
     */
    private fun drawWhitePiece(canvas: Canvas, symbol: String, x: Float, y: Float, isKnight: Boolean = false) {
        val outlineOffset = whitePiecePaint.textSize * 0.03f // Small offset for outline
        
        // For knight, draw details in gray first
        if (isKnight) {
            canvas.drawText(symbol, x, y, whitePieceDetailPaint)
        }
        
        // Draw gray outline by drawing text with small offsets in 8 directions
        val offsets = arrayOf(
            -outlineOffset to 0f,      // left
            outlineOffset to 0f,       // right
            0f to -outlineOffset,      // top
            0f to outlineOffset,       // bottom
            -outlineOffset to -outlineOffset, // top-left
            outlineOffset to -outlineOffset,   // top-right
            -outlineOffset to outlineOffset,  // bottom-left
            outlineOffset to outlineOffset      // bottom-right
        )
        
        for ((dx, dy) in offsets) {
            canvas.drawText(symbol, x + dx, y + dy, whitePieceOutlinePaint)
        }
        
        // Draw white fill on top
        canvas.drawText(symbol, x, y, whitePiecePaint)
    }
    
    private val blackPiecePaint = Paint().apply {
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        color = Color.BLACK
    }

    private val selectedPiecePaint = Paint().apply {
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        color = Color.parseColor("#FFD700") // Gold color for selected piece
        style = Paint.Style.FILL
    }

    private val selectedPieceBackgroundPaint = Paint().apply {
        color = Color.parseColor("#7FC3FF")
        style = Paint.Style.FILL
        alpha = 200
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
        // Clear castling highlight when setting a new selection
        castlingKingSquare = null
        invalidate()
    }

    fun setPossibleMoves(moves: List<Square>) {
        possibleMoves = moves.toMutableList()
        invalidate()
    }
    
    fun clearCastlingHighlight() {
        castlingKingSquare = null
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        
        // Calculate available space with padding
        val padding = (paddingLeft + paddingRight).coerceAtLeast(paddingTop + paddingBottom)
        val availableWidth = widthSize - padding
        val availableHeight = heightSize - padding
        
        // Make it square, fitting within available space
        val size = when {
            widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY -> {
                // Both dimensions are fixed - use the smaller one to maintain square
                Math.min(availableWidth, availableHeight).coerceAtLeast(200) // Minimum 200dp
            }
            widthMode == MeasureSpec.EXACTLY -> {
                // Width is fixed - use it, but respect height constraint
                Math.min(availableWidth, if (heightMode == MeasureSpec.AT_MOST) availableHeight else availableWidth)
                    .coerceAtLeast(200)
            }
            heightMode == MeasureSpec.EXACTLY -> {
                // Height is fixed - use it, but respect width constraint
                Math.min(availableHeight, if (widthMode == MeasureSpec.AT_MOST) availableWidth else availableHeight)
                    .coerceAtLeast(200)
            }
            else -> {
                // Use available space, prefer width
                Math.min(availableWidth, availableHeight).coerceAtLeast(200)
            }
        }
        
        setMeasuredDimension(size + padding, size + padding)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Use the smaller dimension to ensure square board, accounting for padding
        val availableWidth = w - paddingLeft - paddingRight
        val availableHeight = h - paddingTop - paddingBottom
        boardSize = Math.min(availableWidth, availableHeight)
        squareSize = boardSize / 8f
        val textSize = squareSize * 0.7f
        piecePaint.textSize = textSize
        whitePiecePaint.textSize = textSize
        whitePieceOutlinePaint.textSize = textSize
        whitePieceDetailPaint.textSize = textSize
        blackPiecePaint.textSize = textSize
        selectedPiecePaint.textSize = textSize
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Account for padding
        val offsetX = paddingLeft.toFloat()
        val offsetY = paddingTop.toFloat()

        // Draw board squares
        for (row in 0..7) {
            for (col in 0..7) {
                val left = offsetX + col * squareSize
                val top = offsetY + row * squareSize
                val right = left + squareSize
                val bottom = top + squareSize

                val isLight = (row + col) % 2 == 0
                val squarePaint = if (isLight) lightSquarePaint else darkSquarePaint

                // Draw possible move highlights (light background only)
                if (possibleMoves.any { it.row == row && it.col == col }) {
                    canvas.drawRect(left, top, right, bottom, possibleMovePaint)
                } else {
                    canvas.drawRect(left, top, right, bottom, squarePaint)
                }

                // Draw border
                canvas.drawRect(left, top, right, bottom, borderPaint)

                // Draw piece
                val piece = game?.getPieceAt(row, col)
                if (piece != null) {
                    val x = left + squareSize / 2
                    val y = top + squareSize / 2 + piecePaint.textSize / 3
                    
                    // If this piece is selected, highlight it with background circle and gold color
                    if (selectedSquare?.row == row && selectedSquare?.col == col) {
                        // Draw background circle for selected piece
                        val radius = squareSize * 0.45f
                        canvas.drawCircle(x, y - piecePaint.textSize / 3, radius, selectedPieceBackgroundPaint)
                        // Draw piece in gold color with stroke for better visibility
                        if (piece.color == PieceColor.WHITE) {
                            // Draw white piece with gray outline/details and white fill even when selected
                            drawWhitePiece(canvas, piece.getUnicodeSymbol(), x, y, piece.type == PieceType.KNIGHT)
                        } else {
                            selectedPiecePaint.style = Paint.Style.FILL_AND_STROKE
                            selectedPiecePaint.strokeWidth = 3f
                            selectedPiecePaint.color = Color.parseColor("#FFD700") // Gold
                            canvas.drawText(piece.getUnicodeSymbol(), x, y, selectedPiecePaint)
                        }
                    } else if (castlingKingSquare?.row == row && castlingKingSquare?.col == col) {
                        // Highlight king when rook is selected for castling
                        val radius = squareSize * 0.45f
                        canvas.drawCircle(x, y - piecePaint.textSize / 3, radius, selectedPieceBackgroundPaint)
                        // Draw piece with highlight color
                        if (piece.color == PieceColor.WHITE) {
                            // Draw white piece with gray outline/details and white fill
                            drawWhitePiece(canvas, piece.getUnicodeSymbol(), x, y, piece.type == PieceType.KNIGHT)
                        } else {
                            canvas.drawText(piece.getUnicodeSymbol(), x, y, blackPiecePaint)
                        }
                    } else {
                        // Draw piece normally with appropriate color
                        if (piece.color == PieceColor.WHITE) {
                            // Draw white piece with gray outline/details and white fill
                            drawWhitePiece(canvas, piece.getUnicodeSymbol(), x, y, piece.type == PieceType.KNIGHT)
                        } else {
                            canvas.drawText(piece.getUnicodeSymbol(), x, y, blackPiecePaint)
                        }
                    }
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) return false
        
        // Account for padding when calculating touch coordinates
        val touchX = event.x - paddingLeft
        val touchY = event.y - paddingTop
        
        val col = (touchX / squareSize).toInt()
        val row = (touchY / squareSize).toInt()

        // Only handle touches within board bounds
        if (row !in 0..7 || col !in 0..7) {
            return false // Let parent handle touches outside board
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val square = Square(row, col)

                if (selectedSquare == null) {
                    // Select a piece
                    val piece = game?.getPieceAt(row, col)
                    if (piece != null) {
                        selectedSquare = square
                        // Calculate valid moves for this piece
                        possibleMoves = game?.getValidMovesForPiece(square)?.toMutableList() ?: mutableListOf()
                        
                        // If rook is selected on edge, check if castling is possible
                        castlingKingSquare = null
                        if (piece.type == PieceType.ROOK) {
                            val isWhite = piece.color == PieceColor.WHITE
                            val kingRow = if (isWhite) 7 else 0
                            if (square.row == kingRow) {
                                val kingSquare = Square(kingRow, 4)
                                val king = game?.getPieceAt(kingRow, 4)
                                if (king != null && king.type == PieceType.KING && king.color == piece.color) {
                                    val isKingside = square.col == 7
                                    val isQueenside = square.col == 0
                                    
                                    // Check if castling is possible
                                    val castlingPossible = if (isKingside) {
                                        game?.let { g ->
                                            val kingPiece = g.getPieceAt(kingRow, 4)
                                            kingPiece != null && g.isCastlingPossibleForRook(kingPiece, true)
                                        } ?: false
                                    } else if (isQueenside) {
                                        game?.let { g ->
                                            val kingPiece = g.getPieceAt(kingRow, 4)
                                            kingPiece != null && g.isCastlingPossibleForRook(kingPiece, false)
                                        } ?: false
                                    } else {
                                        false
                                    }
                                    
                                    if (castlingPossible) {
                                        castlingKingSquare = kingSquare
                                        // Immediately show castling confirmation dialog
                                        // If user confirms, castling will execute automatically
                                        onCastlingCheckListener?.invoke(square, piece, isKingside)
                                        // Clear selection temporarily - will be restored if user says "No"
                                        selectedSquare = null
                                        possibleMoves.clear()
                                        invalidate()
                                        return true
                                    }
                                }
                            }
                        }
                        
                        invalidate()
                        performClick()
                        return true
                    }
                } else {
                    // Try to make a move
                    if (selectedSquare != square) {
                        // Pass a callback to know if move succeeded
                        onMoveListener?.invoke(selectedSquare!!, square)
                        performClick()
                        return true
                    } else {
                        // Clicked same square - deselect
                        selectedSquare = null
                        possibleMoves.clear()
                        castlingKingSquare = null
                        invalidate()
                        performClick()
                        return true
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }
    
    fun clearSelection() {
        selectedSquare = null
        possibleMoves.clear()
        castlingKingSquare = null
        invalidate()
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}






