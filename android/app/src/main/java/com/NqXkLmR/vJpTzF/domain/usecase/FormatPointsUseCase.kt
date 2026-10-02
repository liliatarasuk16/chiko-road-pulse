package com.NqXkLmR.vJpTzF.domain.usecase

class FormatPointsUseCase {

    operator fun invoke(value: Int): String {
        val digits = value.coerceAtLeast(0).toString()
        if (digits.length <= 3) {
            return digits
        }
        val builder = StringBuilder()
        val offset = digits.length % 3
        if (offset > 0) {
            builder.append(digits, 0, offset)
        }
        var index = offset
        while (index < digits.length) {
            if (builder.isNotEmpty()) {
                builder.append(' ')
            }
            builder.append(digits, index, index + 3)
            index += 3
        }
        return builder.toString()
    }
}
