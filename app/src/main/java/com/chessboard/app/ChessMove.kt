package com.chessboard.app

data class ChessMove(
    val from: Square,
    val to: Square,
    val piece: ChessPiece,
    val capturedPiece: ChessPiece? = null,
    val isPromotion: Boolean = false,
    val promotionType: PieceType? = null,
    val isCastling: Boolean = false,
    val isEnPassant: Boolean = false,
    val moveNumber: Int
) {
    fun toNotation(): String {
        // Castling notation
        if (isCastling) {
            // Kingside castling: king moves from e1/e8 to g1/g8
            if (to.col == 6) {
                return "O-O"
            }
            // Queenside castling: king moves from e1/e8 to c1/c8
            if (to.col == 2) {
                return "O-O-O"
            }
        }
        
        val fromNotation = "${('a' + from.col)}${8 - from.row}"
        val toNotation = "${('a' + to.col)}${8 - to.row}"
        return "$fromNotation-$toNotation"
    }
}

data class Square(val row: Int, val col: Int) {
    fun isValid(): Boolean = row in 0..7 && col in 0..7
}






