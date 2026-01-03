package com.chessboard.app

// Result class for move operations
sealed class MoveResult {
    data class Success(val move: ChessMove) : MoveResult()
    data class PromotionRequired(val from: Square, val to: Square, val piece: ChessPiece) : MoveResult()
    data class CastlingConfirmationRequired(val from: Square, val to: Square, val piece: ChessPiece, val isKingside: Boolean) : MoveResult()
    object Invalid : MoveResult()
}

class ChessGame {
    private val board = Array(8) { Array<ChessPiece?>(8) { null } }
    private val moveHistory = mutableListOf<ChessMove>()
    private var currentMoveIndex = -1
    private var isWhiteTurn = true
    private var isPaused = false
    private var isAutoPlaying = false
    
    // Track captured pieces for each player
    private val whiteCapturedPieces = mutableListOf<ChessPiece>()
    private val blackCapturedPieces = mutableListOf<ChessPiece>()
    
    // Track if pieces have moved (for castling)
    private var whiteKingMoved = false
    private var whiteKingsideRookMoved = false  // h1 rook
    private var whiteQueensideRookMoved = false // a1 rook
    private var blackKingMoved = false
    private var blackKingsideRookMoved = false  // h8 rook
    private var blackQueensideRookMoved = false // a8 rook

    init {
        initializeBoard()
    }

    private fun initializeBoard() {
        // Clear board
        for (i in 0..7) {
            for (j in 0..7) {
                board[i][j] = null
            }
        }

        // Place white pieces
        board[7][0] = ChessPiece(PieceType.ROOK, PieceColor.WHITE)
        board[7][1] = ChessPiece(PieceType.KNIGHT, PieceColor.WHITE)
        board[7][2] = ChessPiece(PieceType.BISHOP, PieceColor.WHITE)
        board[7][3] = ChessPiece(PieceType.QUEEN, PieceColor.WHITE)
        board[7][4] = ChessPiece(PieceType.KING, PieceColor.WHITE)
        board[7][5] = ChessPiece(PieceType.BISHOP, PieceColor.WHITE)
        board[7][6] = ChessPiece(PieceType.KNIGHT, PieceColor.WHITE)
        board[7][7] = ChessPiece(PieceType.ROOK, PieceColor.WHITE)
        for (j in 0..7) {
            board[6][j] = ChessPiece(PieceType.PAWN, PieceColor.WHITE)
        }

        // Place black pieces
        board[0][0] = ChessPiece(PieceType.ROOK, PieceColor.BLACK)
        board[0][1] = ChessPiece(PieceType.KNIGHT, PieceColor.BLACK)
        board[0][2] = ChessPiece(PieceType.BISHOP, PieceColor.BLACK)
        board[0][3] = ChessPiece(PieceType.QUEEN, PieceColor.BLACK)
        board[0][4] = ChessPiece(PieceType.KING, PieceColor.BLACK)
        board[0][5] = ChessPiece(PieceType.BISHOP, PieceColor.BLACK)
        board[0][6] = ChessPiece(PieceType.KNIGHT, PieceColor.BLACK)
        board[0][7] = ChessPiece(PieceType.ROOK, PieceColor.BLACK)
        for (j in 0..7) {
            board[1][j] = ChessPiece(PieceType.PAWN, PieceColor.BLACK)
        }

        moveHistory.clear()
        currentMoveIndex = -1
        isWhiteTurn = true
        whiteCapturedPieces.clear()
        blackCapturedPieces.clear()
        
        // Reset castling flags
        whiteKingMoved = false
        whiteKingsideRookMoved = false
        whiteQueensideRookMoved = false
        blackKingMoved = false
        blackKingsideRookMoved = false
        blackQueensideRookMoved = false
    }

    fun getPieceAt(row: Int, col: Int): ChessPiece? = board[row][col]
    
    fun getCapturedPieces(color: PieceColor): List<ChessPiece> {
        return when (color) {
            PieceColor.WHITE -> whiteCapturedPieces.toList()
            PieceColor.BLACK -> blackCapturedPieces.toList()
        }
    }

    fun makeMove(from: Square, to: Square): MoveResult {
        // Allow moves when not auto-playing (pause state doesn't block manual moves)
        if (isAutoPlaying) return MoveResult.Invalid
        if (!from.isValid() || !to.isValid()) return MoveResult.Invalid

        val piece = board[from.row][from.col] ?: return MoveResult.Invalid
        if ((piece.color == PieceColor.WHITE) != isWhiteTurn) return MoveResult.Invalid

        // Check for castling FIRST (before normal move validation)
        // Castling is an exception: king moves 2 squares, which is not a normal king move
        // Ask for confirmation before executing castling
        if (piece.type == PieceType.KING) {
            val castlingCheck = checkCastlingPossible(piece, from, to)
            if (castlingCheck != null) {
                // Return confirmation required instead of executing directly
                return MoveResult.CastlingConfirmationRequired(from, to, piece, castlingCheck)
            }
        } else if (piece.type == PieceType.ROOK) {
            // Check if rook is initiating castling by selecting castling destination
            val castlingCheck = checkCastlingFromRookPossible(piece, from, to)
            if (castlingCheck != null) {
                // Return confirmation required instead of executing directly
                return MoveResult.CastlingConfirmationRequired(castlingCheck.kingFrom, castlingCheck.kingTo, castlingCheck.king, castlingCheck.isKingside)
            }
        }

        // Basic move validation (allows king to move on any row, including 0 and 7)
        // Note: Castling moves are handled above, so they won't reach this validation
        if (!isValidMove(piece, from, to)) return MoveResult.Invalid

        val capturedPiece = board[to.row][to.col]
        
        // Check if this is a pawn promotion
        val isPromotion = piece.type == PieceType.PAWN && 
                         ((piece.color == PieceColor.WHITE && to.row == 0) || 
                          (piece.color == PieceColor.BLACK && to.row == 7))
        
        if (isPromotion) {
            // Return promotion required result
            return MoveResult.PromotionRequired(from, to, piece)
        }
        
        // Track king and rook moves for castling eligibility
        // Any king move (including on rows 0 and 7) will disable castling
        trackPieceMovement(piece, from)
        
        // Add captured piece to the appropriate list
        if (capturedPiece != null) {
            if (capturedPiece.color == PieceColor.WHITE) {
                whiteCapturedPieces.add(capturedPiece)
            } else {
                blackCapturedPieces.add(capturedPiece)
            }
        }
        
        board[to.row][to.col] = piece
        board[from.row][from.col] = null

        val move = ChessMove(
            from = from,
            to = to,
            piece = piece,
            capturedPiece = capturedPiece,
            isPromotion = false,
            promotionType = null,
            isCastling = false,
            moveNumber = moveHistory.size + 1
        )

        // Remove moves after current index if we're in the middle of history
        if (currentMoveIndex < moveHistory.size - 1) {
            moveHistory.subList(currentMoveIndex + 1, moveHistory.size).clear()
        }

        moveHistory.add(move)
        currentMoveIndex = moveHistory.size - 1
        isWhiteTurn = !isWhiteTurn

        return MoveResult.Success(move)
    }
    
    private fun trackPieceMovement(piece: ChessPiece, from: Square) {
        when {
            piece.type == PieceType.KING && piece.color == PieceColor.WHITE -> {
                // Any king move (including on rows 0-7) disables castling
                whiteKingMoved = true
            }
            piece.type == PieceType.KING && piece.color == PieceColor.BLACK -> {
                // Any king move (including on rows 0-7) disables castling
                blackKingMoved = true
            }
            piece.type == PieceType.ROOK && piece.color == PieceColor.WHITE -> {
                when (from.row to from.col) {
                    7 to 7 -> whiteKingsideRookMoved = true  // h1
                    7 to 0 -> whiteQueensideRookMoved = true // a1
                }
            }
            piece.type == PieceType.ROOK && piece.color == PieceColor.BLACK -> {
                when (from.row to from.col) {
                    0 to 7 -> blackKingsideRookMoved = true  // h8
                    0 to 0 -> blackQueensideRookMoved = true // a8
                }
            }
        }
    }
    
    private fun trackPieceMovementForReplay(move: ChessMove) {
        val piece = move.piece
        val from = move.from
        
        if (move.isCastling) {
            // Castling moves both king and rook
            if (piece.color == PieceColor.WHITE) {
                whiteKingMoved = true
                if (move.to.col == 6) {
                    whiteKingsideRookMoved = true
                } else {
                    whiteQueensideRookMoved = true
                }
            } else {
                blackKingMoved = true
                if (move.to.col == 6) {
                    blackKingsideRookMoved = true
                } else {
                    blackQueensideRookMoved = true
                }
            }
        } else {
            trackPieceMovement(piece, from)
        }
    }
    
    fun completePromotion(from: Square, to: Square, promotionType: PieceType): MoveResult {
        if (isPaused || isAutoPlaying) return MoveResult.Invalid
        if (!from.isValid() || !to.isValid()) return MoveResult.Invalid
        
        val piece = board[from.row][from.col] ?: return MoveResult.Invalid
        if (piece.type != PieceType.PAWN) return MoveResult.Invalid
        if ((piece.color == PieceColor.WHITE) != isWhiteTurn) return MoveResult.Invalid
        
        // Don't allow promoting to pawn or king
        if (promotionType == PieceType.PAWN || promotionType == PieceType.KING) {
            return MoveResult.Invalid
        }
        
        val capturedPieces = if (piece.color == PieceColor.WHITE) whiteCapturedPieces else blackCapturedPieces
        
        // Must have captured pieces to promote - no fallback to standard pieces
        if (capturedPieces.isEmpty()) {
            return MoveResult.Invalid
        }
        
        // Must use a captured piece of the chosen type
        val availableTypes = capturedPieces.map { it.type }
        if (promotionType !in availableTypes) {
            return MoveResult.Invalid
        }
        
        val capturedPiece = board[to.row][to.col]
        
        // DO NOT remove captured pieces - they can be reused for multiple promotions
        // For example, if you captured 1 queen, you can promote multiple pawns to queens
        
        // Add captured piece from this move to the appropriate list
        if (capturedPiece != null) {
            if (capturedPiece.color == PieceColor.WHITE) {
                whiteCapturedPieces.add(capturedPiece)
            } else {
                blackCapturedPieces.add(capturedPiece)
            }
        }
        
        // Track the original pawn movement (before promotion)
        trackPieceMovement(piece, from)
        
        // Create the promoted piece
        val promotedPiece = ChessPiece(promotionType, piece.color)
        board[to.row][to.col] = promotedPiece
        board[from.row][from.col] = null

        val move = ChessMove(
            from = from,
            to = to,
            piece = promotedPiece,
            capturedPiece = capturedPiece,
            isPromotion = true,
            promotionType = promotionType,
            isCastling = false,
            moveNumber = moveHistory.size + 1
        )

        // Remove moves after current index if we're in the middle of history
        if (currentMoveIndex < moveHistory.size - 1) {
            moveHistory.subList(currentMoveIndex + 1, moveHistory.size).clear()
        }

        moveHistory.add(move)
        currentMoveIndex = moveHistory.size - 1
        isWhiteTurn = !isWhiteTurn

        return MoveResult.Success(move)
    }

    private fun isValidMove(piece: ChessPiece, from: Square, to: Square): Boolean {
        val targetPiece = board[to.row][to.col]
        // Cannot capture friendly pieces
        if (targetPiece != null && targetPiece.color == piece.color) return false

        // For all pieces except pawns: if they can move to a square, they can capture opponent pieces there
        // Pawns are special: they can only capture diagonally, not forward
        return when (piece.type) {
            PieceType.PAWN -> isValidPawnMove(piece, from, to)
            PieceType.ROOK -> isValidRookMove(from, to)
            PieceType.KNIGHT -> isValidKnightMove(from, to)
            PieceType.BISHOP -> isValidBishopMove(from, to)
            PieceType.QUEEN -> isValidRookMove(from, to) || isValidBishopMove(from, to)
            PieceType.KING -> isValidKingMove(from, to)
        }
    }

    private fun isValidPawnMove(piece: ChessPiece, from: Square, to: Square): Boolean {
        val direction = if (piece.color == PieceColor.WHITE) -1 else 1
        val startRow = if (piece.color == PieceColor.WHITE) 6 else 1

        // Forward move: pawns can only move forward to empty squares, never capture forward
        if (from.col == to.col) {
            // Single square forward (must be empty)
            if (to.row == from.row + direction && board[to.row][to.col] == null) return true
            // Double square forward from starting position (both squares must be empty)
            if (from.row == startRow && to.row == from.row + 2 * direction && 
                board[to.row][to.col] == null && board[from.row + direction][to.col] == null) return true
        }
        // Capture: pawns can only capture diagonally (left or right forward)
        // The destination must have an opponent piece (already checked in isValidMove that it's not friendly)
        if (Math.abs(from.col - to.col) == 1 && to.row == from.row + direction) {
            return board[to.row][to.col] != null
        }
        return false
    }

    private fun isValidRookMove(from: Square, to: Square): Boolean {
        if (from.row != to.row && from.col != to.col) return false
        return isPathClear(from, to)
    }

    private fun isValidKnightMove(from: Square, to: Square): Boolean {
        // Knights move in an L-shape: 2 squares in one direction, 1 square perpendicular
        // Knights can jump over pieces, so we don't check the path
        val rowDiff = Math.abs(from.row - to.row)
        val colDiff = Math.abs(from.col - to.col)
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)
    }

    private fun isValidBishopMove(from: Square, to: Square): Boolean {
        if (Math.abs(from.row - to.row) != Math.abs(from.col - to.col)) return false
        return isPathClear(from, to)
    }

    private fun isValidKingMove(from: Square, to: Square): Boolean {
        val rowDiff = Math.abs(from.row - to.row)
        val colDiff = Math.abs(from.col - to.col)
        // Normal king move: one square in any direction (including on rows 0 and 7)
        // When the king moves (anywhere), castling will be disabled via trackPieceMovement
        if (rowDiff <= 1 && colDiff <= 1) return true
        
        // Check for castling moves (2 squares horizontally on back rank)
        // King must be on starting square (e1/e8 = row 7/0, col 4)
        val piece = board[from.row][from.col] ?: return false
        val isWhite = piece.color == PieceColor.WHITE
        val kingRow = if (isWhite) 7 else 0
        
        if (from.row == kingRow && from.col == 4) {
            // Kingside castling: king moves to g1/g8 (col 6)
            if (to.row == kingRow && to.col == 6) {
                return isCastlingPossible(piece, true) // true = kingside
            }
            // Queenside castling: king moves to a1/a8 (col 0) - edge square
            if (to.row == kingRow && to.col == 0) {
                return isCastlingPossible(piece, false) // false = queenside
            }
        }
        
        return false
    }
    
    fun isCastlingPossibleForRook(king: ChessPiece, kingside: Boolean): Boolean {
        return isCastlingPossible(king, kingside)
    }
    
    private fun isCastlingPossible(piece: ChessPiece, kingside: Boolean): Boolean {
        val isWhite = piece.color == PieceColor.WHITE
        val kingRow = if (isWhite) 7 else 0
        
        // Check if castling is still allowed
        if (isWhite) {
            if (whiteKingMoved) return false
            if (kingside && whiteKingsideRookMoved) return false
            if (!kingside && whiteQueensideRookMoved) return false
        } else {
            if (blackKingMoved) return false
            if (kingside && blackKingsideRookMoved) return false
            if (!kingside && blackQueensideRookMoved) return false
        }
        
        if (kingside) {
            // Check if rook is in place (h1/h8 = col 7)
            val rookSquare = Square(kingRow, 7)
            val rook = board[rookSquare.row][rookSquare.col]
            if (rook == null || rook.type != PieceType.ROOK || rook.color != piece.color) {
                return false
            }
            // Check if path is clear (squares f1/f8 and g1/g8 = cols 5 and 6)
            if (board[kingRow][5] != null || board[kingRow][6] != null) {
                return false
            }
            // Check if king is in check
            val kingSquare = Square(kingRow, 4)
            if (isSquareUnderAttack(kingSquare, piece.color)) {
                return false
            }
            // Check if squares the king passes through are under attack
            val intermediateSquare = Square(kingRow, 5)
            val destinationSquare = Square(kingRow, 6)
            if (isSquareUnderAttack(intermediateSquare, piece.color) || 
                isSquareUnderAttack(destinationSquare, piece.color)) {
                return false
            }
        } else {
            // Check if rook is in place (a1/a8 = col 0)
            val rookSquare = Square(kingRow, 0)
            val rook = board[rookSquare.row][rookSquare.col]
            if (rook == null || rook.type != PieceType.ROOK || rook.color != piece.color) {
                return false
            }
            // Check if path is clear (squares b1/b8, c1/c8, d1/d8 = cols 1, 2, 3)
            if (board[kingRow][1] != null || board[kingRow][2] != null || board[kingRow][3] != null) {
                return false
            }
            // Check if king is in check
            val kingSquare = Square(kingRow, 4)
            if (isSquareUnderAttack(kingSquare, piece.color)) {
                return false
            }
            // Check if squares the king passes through are under attack
            // King moves from e1/e8 (col 4) to a1/a8 (col 0), passing through d1/d8 (col 3), c1/c8 (col 2), b1/b8 (col 1)
            val intermediateSquare1 = Square(kingRow, 3)
            val intermediateSquare2 = Square(kingRow, 2)
            val intermediateSquare3 = Square(kingRow, 1)
            val kingDestination = Square(kingRow, 0)  // King goes to edge: a1/a8
            if (isSquareUnderAttack(intermediateSquare1, piece.color) || 
                isSquareUnderAttack(intermediateSquare2, piece.color) ||
                isSquareUnderAttack(intermediateSquare3, piece.color) ||
                isSquareUnderAttack(kingDestination, piece.color)) {
                return false
            }
        }
        
        return true
    }
    
    private fun checkCastlingPossible(piece: ChessPiece, from: Square, to: Square): Boolean? {
        if (piece.type != PieceType.KING) return null
        
        val isWhite = piece.color == PieceColor.WHITE
        val kingRow = if (isWhite) 7 else 0
        
        // King must be on its starting square
        if (from.row != kingRow || from.col != 4) return null
        
        // Check for kingside castling (O-O): king moves 2 squares right
        if (to.row == kingRow && to.col == 6) {
            return if (isCastlingPossible(piece, true)) true else null
        }
        
        // Check for queenside castling (O-O-O): king moves 2 squares left
        if (to.row == kingRow && to.col == 2) {
            return if (isCastlingPossible(piece, false)) false else null
        }
        
        return null
    }
    
    // Helper class for castling information
    private data class CastlingInfo(val kingFrom: Square, val kingTo: Square, val king: ChessPiece, val isKingside: Boolean)
    
    private fun checkCastlingFromRookPossible(rook: ChessPiece, rookFrom: Square, to: Square): CastlingInfo? {
        val isWhite = rook.color == PieceColor.WHITE
        val kingRow = if (isWhite) 7 else 0
        
        // Rook must be on back rank
        if (rookFrom.row != kingRow) return null
        
        // Find the king
        val kingSquare = Square(kingRow, 4)
        val king = board[kingSquare.row][kingSquare.col]
        if (king == null || king.type != PieceType.KING || king.color != rook.color) {
            return null
        }
        
        // Check if this is a castling move destination
        // Kingside castling: rook is on h1/h8 (col 7), tap g1/g8 (col 6) to castle
        if (rookFrom.col == 7 && to.row == kingRow && to.col == 6) {
            // Check if castling is possible
            if (isCastlingPossible(king, true)) {
                return CastlingInfo(kingSquare, Square(kingRow, 6), king, true)
            }
        }
        
        // Queenside castling: rook is on a1/a8 (col 0), king moves to a1/a8 (col 0) - edge square
        // User selects rook and taps the rook's square to execute queenside castling
        if (rookFrom.col == 0 && to.row == kingRow && to.col == 0 && rookFrom != to) {
            // Check if castling is possible
            if (isCastlingPossible(king, false)) {
                return CastlingInfo(kingSquare, Square(kingRow, 0), king, false)
            }
        }
        
        return null
    }
    
    // Execute castling after confirmation
    fun executeCastling(from: Square, to: Square, isKingside: Boolean): MoveResult {
        val piece = board[from.row][from.col] ?: return MoveResult.Invalid
        if (piece.type != PieceType.KING) return MoveResult.Invalid
        
        return if (isKingside) {
            executeKingsideCastling(piece, from, to) ?: MoveResult.Invalid
        } else {
            executeQueensideCastling(piece, from, to) ?: MoveResult.Invalid
        }
    }
    
    // Try normal move validation (used when castling is declined)
    fun tryNormalMove(from: Square, to: Square): MoveResult {
        // Allow moves when not auto-playing (pause state doesn't block manual moves)
        if (isAutoPlaying) return MoveResult.Invalid
        if (!from.isValid() || !to.isValid()) return MoveResult.Invalid

        val piece = board[from.row][from.col] ?: return MoveResult.Invalid
        if ((piece.color == PieceColor.WHITE) != isWhiteTurn) return MoveResult.Invalid

        // Skip castling check - just validate as normal move
        if (!isValidMove(piece, from, to)) return MoveResult.Invalid

        val capturedPiece = board[to.row][to.col]
        
        // Check if this is a pawn promotion
        val isPromotion = piece.type == PieceType.PAWN && 
                         ((piece.color == PieceColor.WHITE && to.row == 0) || 
                          (piece.color == PieceColor.BLACK && to.row == 7))
        
        if (isPromotion) {
            // Return promotion required result
            return MoveResult.PromotionRequired(from, to, piece)
        }
        
        // Track king and rook moves for castling eligibility
        // Any king move (including on rows 0 and 7) will disable castling
        trackPieceMovement(piece, from)
        
        // Add captured piece to the appropriate list
        if (capturedPiece != null) {
            if (capturedPiece.color == PieceColor.WHITE) {
                whiteCapturedPieces.add(capturedPiece)
            } else {
                blackCapturedPieces.add(capturedPiece)
            }
        }
        
        board[to.row][to.col] = piece
        board[from.row][from.col] = null

        val move = ChessMove(
            from = from,
            to = to,
            piece = piece,
            capturedPiece = capturedPiece,
            isPromotion = false,
            promotionType = null,
            isCastling = false,
            moveNumber = moveHistory.size + 1
        )

        // Remove moves after current index if we're in the middle of history
        if (currentMoveIndex < moveHistory.size - 1) {
            moveHistory.subList(currentMoveIndex + 1, moveHistory.size).clear()
        }

        moveHistory.add(move)
        currentMoveIndex = moveHistory.size - 1
        isWhiteTurn = !isWhiteTurn

        return MoveResult.Success(move)
    }
    
    private fun executeKingsideCastling(piece: ChessPiece, from: Square, to: Square): MoveResult? {
        val isWhite = piece.color == PieceColor.WHITE
        val kingRow = if (isWhite) 7 else 0
        
        // Check if castling is still allowed
        if (isWhite) {
            if (whiteKingMoved || whiteKingsideRookMoved) return null
        } else {
            if (blackKingMoved || blackKingsideRookMoved) return null
        }
        
        // Check if rook is in place (h1 for white, h8 for black)
        val rookSquare = Square(kingRow, 7)
        val rook = board[rookSquare.row][rookSquare.col]
        if (rook == null || rook.type != PieceType.ROOK || rook.color != piece.color) {
            return null
        }
        
        // Check if path is clear (squares between king and rook: f1, g1 for white)
        if (board[kingRow][5] != null || board[kingRow][6] != null) {
            return null
        }
        
        // Check if king is in check
        if (isSquareUnderAttack(from, piece.color)) {
            return null
        }
        
        // Check if squares the king passes through are under attack
        val intermediateSquare = Square(kingRow, 5)
        if (isSquareUnderAttack(intermediateSquare, piece.color)) {
            return null
        }
        
        // Check if destination square is under attack
        if (isSquareUnderAttack(to, piece.color)) {
            return null
        }
        
        // Execute castling: move king and rook
        // IMPORTANT: Clear original positions first, then place pieces
        // King moves to the edge: g1/g8 (col 6)
        board[from.row][from.col] = null  // Clear king's original position first
        board[rookSquare.row][rookSquare.col] = null  // Clear rook's original position
        board[to.row][to.col] = piece  // Place king at destination
        board[kingRow][5] = rook  // Rook moves to f1/f8
        
        // Mark pieces as moved
        if (isWhite) {
            whiteKingMoved = true
            whiteKingsideRookMoved = true
        } else {
            blackKingMoved = true
            blackKingsideRookMoved = true
        }
        
        val move = ChessMove(
            from = from,
            to = to,
            piece = piece,
            capturedPiece = null,
            isPromotion = false,
            promotionType = null,
            isCastling = true,
            moveNumber = moveHistory.size + 1
        )
        
        // Remove moves after current index if we're in the middle of history
        if (currentMoveIndex < moveHistory.size - 1) {
            moveHistory.subList(currentMoveIndex + 1, moveHistory.size).clear()
        }
        
        moveHistory.add(move)
        currentMoveIndex = moveHistory.size - 1
        isWhiteTurn = !isWhiteTurn
        
        return MoveResult.Success(move)
    }
    
    private fun executeQueensideCastling(piece: ChessPiece, from: Square, to: Square): MoveResult? {
        val isWhite = piece.color == PieceColor.WHITE
        val kingRow = if (isWhite) 7 else 0
        
        // Check if castling is still allowed
        if (isWhite) {
            if (whiteKingMoved || whiteQueensideRookMoved) return null
        } else {
            if (blackKingMoved || blackQueensideRookMoved) return null
        }
        
        // Check if rook is in place (a1 for white, a8 for black)
        val rookSquare = Square(kingRow, 0)
        val rook = board[rookSquare.row][rookSquare.col]
        if (rook == null || rook.type != PieceType.ROOK || rook.color != piece.color) {
            return null
        }
        
        // Check if path is clear (squares between king and rook: b1, c1, d1 for white)
        if (board[kingRow][1] != null || board[kingRow][2] != null || board[kingRow][3] != null) {
            return null
        }
        
        // Check if king is in check
        if (isSquareUnderAttack(from, piece.color)) {
            return null
        }
        
        // Check if squares the king passes through are under attack
        // King moves from e1/e8 (col 4) to a1/a8 (col 0), passing through d1/d8 (col 3), c1/c8 (col 2), b1/b8 (col 1)
        val intermediateSquare1 = Square(kingRow, 3)
        val intermediateSquare2 = Square(kingRow, 2)
        val intermediateSquare3 = Square(kingRow, 1)
        val kingDestination = Square(kingRow, 0)  // King goes to edge: a1/a8
        if (isSquareUnderAttack(intermediateSquare1, piece.color) || 
            isSquareUnderAttack(intermediateSquare2, piece.color) ||
            isSquareUnderAttack(intermediateSquare3, piece.color) ||
            isSquareUnderAttack(kingDestination, piece.color)) {
            return null
        }
        
        // Execute castling: move king and rook
        // IMPORTANT: Clear rook square first, then move pieces
        // King moves to the edge: a1/a8 (col 0)
        board[from.row][from.col] = null  // Clear king's original position first
        board[rookSquare.row][rookSquare.col] = null  // Clear rook's original position
        board[kingDestination.row][kingDestination.col] = piece  // Place king at destination
        board[kingRow][3] = rook  // Rook moves to d1/d8
        
        // Mark pieces as moved
        if (isWhite) {
            whiteKingMoved = true
            whiteQueensideRookMoved = true
        } else {
            blackKingMoved = true
            blackQueensideRookMoved = true
        }
        
        val move = ChessMove(
            from = from,
            to = kingDestination,  // King goes to edge: a1/a8
            piece = piece,
            capturedPiece = null,
            isPromotion = false,
            promotionType = null,
            isCastling = true,
            moveNumber = moveHistory.size + 1
        )
        
        // Remove moves after current index if we're in the middle of history
        if (currentMoveIndex < moveHistory.size - 1) {
            moveHistory.subList(currentMoveIndex + 1, moveHistory.size).clear()
        }
        
        moveHistory.add(move)
        currentMoveIndex = moveHistory.size - 1
        isWhiteTurn = !isWhiteTurn
        
        return MoveResult.Success(move)
    }
    
    private fun isSquareUnderAttack(square: Square, byColor: PieceColor): Boolean {
        val opponentColor = if (byColor == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE
        
        // Check all opponent pieces to see if any can attack this square
        for (row in 0..7) {
            for (col in 0..7) {
                val piece = board[row][col]
                if (piece != null && piece.color == opponentColor) {
                    val fromSquare = Square(row, col)
                    // Temporarily clear the target square to check if piece can attack it
                    val originalPiece = board[square.row][square.col]
                    
                    try {
                        board[square.row][square.col] = null
                        
                        val canAttack = when (piece.type) {
                            PieceType.PAWN -> {
                                val direction = if (opponentColor == PieceColor.WHITE) -1 else 1
                                // Pawns attack diagonally
                                (Math.abs(fromSquare.col - square.col) == 1 && 
                                 square.row == fromSquare.row + direction)
                            }
                            PieceType.ROOK -> {
                                (fromSquare.row == square.row || fromSquare.col == square.col) &&
                                isPathClear(fromSquare, square)
                            }
                            PieceType.KNIGHT -> {
                                val rowDiff = Math.abs(fromSquare.row - square.row)
                                val colDiff = Math.abs(fromSquare.col - square.col)
                                (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)
                            }
                            PieceType.BISHOP -> {
                                (Math.abs(fromSquare.row - square.row) == Math.abs(fromSquare.col - square.col)) &&
                                isPathClear(fromSquare, square)
                            }
                            PieceType.QUEEN -> {
                                ((fromSquare.row == square.row || fromSquare.col == square.col) ||
                                 (Math.abs(fromSquare.row - square.row) == Math.abs(fromSquare.col - square.col))) &&
                                isPathClear(fromSquare, square)
                            }
                            PieceType.KING -> {
                                val rowDiff = Math.abs(fromSquare.row - square.row)
                                val colDiff = Math.abs(fromSquare.col - square.col)
                                rowDiff <= 1 && colDiff <= 1
                            }
                        }
                        
                        if (canAttack) {
                            // Restore original piece before returning
                            board[square.row][square.col] = originalPiece
                            return true
                        }
                    } finally {
                        // Always restore original piece, even if an exception occurs
                        board[square.row][square.col] = originalPiece
                    }
                }
            }
        }
        
        return false
    }

    private fun isPathClear(from: Square, to: Square): Boolean {
        val rowStep = if (to.row > from.row) 1 else if (to.row < from.row) -1 else 0
        val colStep = if (to.col > from.col) 1 else if (to.col < from.col) -1 else 0

        var currentRow = from.row + rowStep
        var currentCol = from.col + colStep

        // Check all squares between from and to (excluding the destination square)
        while (currentRow != to.row || currentCol != to.col) {
            // Bounds check to prevent array out of bounds
            if (currentRow < 0 || currentRow > 7 || currentCol < 0 || currentCol > 7) {
                return false
            }
            // If there's a piece blocking the path, the move is invalid
            if (board[currentRow][currentCol] != null) return false
            currentRow += rowStep
            currentCol += colStep
        }
        return true
    }

    fun getMoveHistory(): List<ChessMove> = moveHistory.toList()

    fun getCurrentMoveIndex(): Int = currentMoveIndex

    fun getTotalMoves(): Int = moveHistory.size
    
    fun getValidMovesForPiece(from: Square): List<Square> {
        val piece = board[from.row][from.col] ?: return emptyList()
        if ((piece.color == PieceColor.WHITE) != isWhiteTurn) return emptyList()
        
        val validMoves = mutableListOf<Square>()
        
        // FIRST: Add castling moves (these are special exceptions to normal piece movement)
        // Add castling moves for king if applicable
        if (piece.type == PieceType.KING) {
            val isWhite = piece.color == PieceColor.WHITE
            val kingRow = if (isWhite) 7 else 0
            
            // King must be on starting square (e1/e8)
            if (from.row == kingRow && from.col == 4) {
                // Check kingside castling (O-O): king moves to g1/g8 (col 6)
                if (isCastlingPossible(piece, true)) {
                    validMoves.add(Square(kingRow, 6))
                }
                // Check queenside castling (O-O-O): king moves to a1/a8 (col 0) - edge
                if (isCastlingPossible(piece, false)) {
                    validMoves.add(Square(kingRow, 0))
                }
            }
        }
        
        // Add castling moves for rook if applicable
        // When rook is selected, show where the KING will move to (castling destination)
        if (piece.type == PieceType.ROOK) {
            val isWhite = piece.color == PieceColor.WHITE
            val kingRow = if (isWhite) 7 else 0
            
            // Rook must be on back rank
            if (from.row == kingRow) {
                // Find the king
                val kingSquare = Square(kingRow, 4)
                val king = board[kingSquare.row][kingSquare.col]
                if (king != null && king.type == PieceType.KING && king.color == piece.color) {
                    // Kingside castling: rook on h1/h8 (col 7), show castling destination g1/g8 (col 6)
                    // This is where the KING moves to, not the rook
                    if (from.col == 7 && isCastlingPossible(king, true)) {
                        validMoves.add(Square(kingRow, 6))
                    }
                    // Queenside castling: rook on a1/a8 (col 0), tap a1/a8 (col 0) to castle
                    // Show the rook's own square as the castling destination
                    if (from.col == 0 && isCastlingPossible(king, false)) {
                        validMoves.add(Square(kingRow, 0))
                    }
                }
            }
        }
        
        // THEN: Add normal piece moves
        for (row in 0..7) {
            for (col in 0..7) {
                val to = Square(row, col)
                // Skip if it's the same square or already added as castling move
                if (from == to || validMoves.contains(to)) continue
                if (isValidMove(piece, from, to)) {
                    validMoves.add(to)
                }
            }
        }
        
        return validMoves
    }

    fun goToMove(index: Int) {
        if (index < -1 || index >= moveHistory.size) return

        // Save move history before resetting
        val savedHistory = moveHistory.toList()

        // Reset board to initial position (without clearing history)
        resetBoardOnly()

        // Reset captured pieces
        whiteCapturedPieces.clear()
        blackCapturedPieces.clear()
        
        // Replay moves up to index
        if (index >= 0) {
            for (i in 0..index) {
                val move = savedHistory[i]
                
                if (move.isCastling) {
                    // Handle castling: move both king and rook
                    val isWhite = move.piece.color == PieceColor.WHITE
                    val kingRow = if (isWhite) 7 else 0
                    
                    // Move king
                    board[move.to.row][move.to.col] = move.piece
                    board[move.from.row][move.from.col] = null
                    
                    // Move rook based on castling type
                    if (move.to.col == 6) {
                        // Kingside castling: rook from h1/h8 to f1/f8
                        val rook = ChessPiece(PieceType.ROOK, move.piece.color)
                        board[kingRow][5] = rook
                        board[kingRow][7] = null
                    } else if (move.to.col == 2) {
                        // Queenside castling: rook from a1/a8 to d1/d8
                        val rook = ChessPiece(PieceType.ROOK, move.piece.color)
                        board[kingRow][3] = rook
                        board[kingRow][0] = null
                    }
                } else {
                    // Normal move
                    board[move.to.row][move.to.col] = move.piece
                    board[move.from.row][move.from.col] = null
                }
                
                // Rebuild captured pieces list
                if (move.capturedPiece != null) {
                    if (move.capturedPiece.color == PieceColor.WHITE) {
                        whiteCapturedPieces.add(move.capturedPiece)
                    } else {
                        blackCapturedPieces.add(move.capturedPiece)
                    }
                }
                
                // Track piece movements for castling eligibility
                trackPieceMovementForReplay(move)
                
                isWhiteTurn = move.piece.color == PieceColor.BLACK
            }
        }

        currentMoveIndex = index
    }

    private fun resetBoardOnly() {
        // Clear board
        for (i in 0..7) {
            for (j in 0..7) {
                board[i][j] = null
            }
        }

        // Place white pieces
        board[7][0] = ChessPiece(PieceType.ROOK, PieceColor.WHITE)
        board[7][1] = ChessPiece(PieceType.KNIGHT, PieceColor.WHITE)
        board[7][2] = ChessPiece(PieceType.BISHOP, PieceColor.WHITE)
        board[7][3] = ChessPiece(PieceType.QUEEN, PieceColor.WHITE)
        board[7][4] = ChessPiece(PieceType.KING, PieceColor.WHITE)
        board[7][5] = ChessPiece(PieceType.BISHOP, PieceColor.WHITE)
        board[7][6] = ChessPiece(PieceType.KNIGHT, PieceColor.WHITE)
        board[7][7] = ChessPiece(PieceType.ROOK, PieceColor.WHITE)
        for (j in 0..7) {
            board[6][j] = ChessPiece(PieceType.PAWN, PieceColor.WHITE)
        }

        // Place black pieces
        board[0][0] = ChessPiece(PieceType.ROOK, PieceColor.BLACK)
        board[0][1] = ChessPiece(PieceType.KNIGHT, PieceColor.BLACK)
        board[0][2] = ChessPiece(PieceType.BISHOP, PieceColor.BLACK)
        board[0][3] = ChessPiece(PieceType.QUEEN, PieceColor.BLACK)
        board[0][4] = ChessPiece(PieceType.KING, PieceColor.BLACK)
        board[0][5] = ChessPiece(PieceType.BISHOP, PieceColor.BLACK)
        board[0][6] = ChessPiece(PieceType.KNIGHT, PieceColor.BLACK)
        board[0][7] = ChessPiece(PieceType.ROOK, PieceColor.BLACK)
        for (j in 0..7) {
            board[1][j] = ChessPiece(PieceType.PAWN, PieceColor.BLACK)
        }

        isWhiteTurn = true
        whiteCapturedPieces.clear()
        blackCapturedPieces.clear()
    }

    fun goToFirstMove() = goToMove(-1)
    fun goToLastMove() = goToMove(moveHistory.size - 1)
    fun goToNextMove() = goToMove(currentMoveIndex + 1)
    fun goToPreviousMove() = goToMove(currentMoveIndex - 1)

    fun pause() { isPaused = true; isAutoPlaying = false }
    fun resume() { isPaused = false }
    fun isPaused(): Boolean = isPaused

    fun setAutoPlaying(playing: Boolean) { isAutoPlaying = playing }
    fun isAutoPlaying(): Boolean = isAutoPlaying

    fun reset() {
        initializeBoard()
    }

    fun newGame() {
        initializeBoard()
    }
}

