package com.chessboard.app

enum class PieceType {
    PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING
}

enum class PieceColor {
    WHITE, BLACK
}

data class ChessPiece(
    val type: PieceType,
    val color: PieceColor
) {
    fun getUnicodeSymbol(): String {
        return when (color) {
            PieceColor.WHITE -> when (type) {
                PieceType.PAWN -> "♙"
                PieceType.ROOK -> "♖"
                PieceType.KNIGHT -> "♘"
                PieceType.BISHOP -> "♗"
                PieceType.QUEEN -> "♕"
                PieceType.KING -> "♔"
            }
            PieceColor.BLACK -> when (type) {
                PieceType.PAWN -> "♟"
                PieceType.ROOK -> "♜"
                PieceType.KNIGHT -> "♞"
                PieceType.BISHOP -> "♝"
                PieceType.QUEEN -> "♛"
                PieceType.KING -> "♚"
            }
        }
    }
}






