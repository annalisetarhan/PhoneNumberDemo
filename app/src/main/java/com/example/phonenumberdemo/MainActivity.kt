package com.example.phonenumberdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.example.phonenumberdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpPhoneEditTexts()
    }

    private var digitDeletedFlag = false
    private var userIsNotMakingChanges = false
    private var prevEditText1CursorPosition: Int? = null
    private var prevEditText2CursorPosition: Int? = null

    private fun setUpPhoneEditTexts() {
        binding.phoneEditText1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (userIsNotMakingChanges) {
                    return
                }
                if (binding.phoneEditText1.text.length == 3) {
                    val lastDigit = chopOffLastDigit(1)
                    pushDigitToNextEditText(lastDigit, 2)
                    moveCursorIfNecessary(1)
                }
                if (digitDeletedFlag) {
                    pullDigitFromNextEditText(1)
                    digitDeletedFlag = false
                }
                prevEditText1CursorPosition = null
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p3 < p2 && !userIsNotMakingChanges) {
                    digitDeletedFlag = true
                }
                prevEditText1CursorPosition = binding.phoneEditText1.selectionStart
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        binding.phoneEditText2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (userIsNotMakingChanges) {
                    return
                }
                if (binding.phoneEditText2.selectionEnd == 0) {
                    moveCursorBackTo(1)
                }
                if (binding.phoneEditText2.text.length == 4) {
                    val lastDigit = chopOffLastDigit(2)
                    pushDigitToNextEditText(lastDigit, 3)
                    moveCursorIfNecessary(2)
                }
                if (digitDeletedFlag) {
                    pullDigitFromNextEditText(2)
                    digitDeletedFlag = false
                }
                prevEditText2CursorPosition = null
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p3 < p2 && !userIsNotMakingChanges) {
                    digitDeletedFlag = true
                }
                prevEditText2CursorPosition = binding.phoneEditText2.selectionStart
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        binding.phoneEditText3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (userIsNotMakingChanges) {
                    return
                }
                if (binding.phoneEditText3.selectionEnd == 0) {
                    moveCursorBackTo(2)
                }
                if (binding.phoneEditText3.text.length == 5) {
                    chopOffLastDigit(3)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

    private fun chopOffLastDigit(editTextNum: Int): Char {
        val editText = getEditText(editTextNum)
        val maxLength = getMaxLength(editTextNum)
        val lastDigit = editText.text[maxLength]

        userIsNotMakingChanges = true
        editText.text.delete(maxLength, maxLength + 1)
        userIsNotMakingChanges = false

        return lastDigit
    }

    private fun pushDigitToNextEditText(digit: Char, nextEditTextNum: Int) {
        val nextEditText = getEditText(nextEditTextNum)
        val nextNextEditTextNum = getNextEditTextNum(nextEditTextNum)
        val maxLength = getMaxLength(nextEditTextNum)

        userIsNotMakingChanges = true
        var bumpedDigit: Char? = null
        if (nextEditText.text.length == maxLength) {
            bumpedDigit = nextEditText.text[maxLength - 1]
            nextEditText.text.delete(maxLength - 1, maxLength)
        }
        nextEditText.text.insert(0, digit.toString())
        if (bumpedDigit != null && nextNextEditTextNum != null) {
            pushDigitToNextEditText(bumpedDigit, nextNextEditTextNum)
        }
        userIsNotMakingChanges = false
    }

    private fun moveCursorBackTo(prevEditTextNum: Int) {
        val prevEditText = getEditText(prevEditTextNum)
        prevEditText.requestFocus()
        prevEditText.setSelection(prevEditText.length())
    }

    private fun moveCursorIfNecessary(editTextNum: Int) {
        if (editTextNum == 1 && prevEditText1CursorPosition!! > 1) {
            binding.phoneEditText2.requestFocus()
            binding.phoneEditText2.setSelection(prevEditText1CursorPosition!! - 2)
        } else if (editTextNum == 2 && prevEditText2CursorPosition!! > 2) {
            binding.phoneEditText3.requestFocus()
            binding.phoneEditText3.setSelection(prevEditText2CursorPosition!! - 3)
        }
    }

    private fun pullDigitFromNextEditText(editTextNum: Int) {
        val editText = getEditText(editTextNum)
        val maxLength = getMaxLength(editTextNum)
        val nextEditTextNum = getNextEditTextNum(editTextNum)
        val nextEditText = getNextEditText(editTextNum)

        if (editText.text.length != maxLength - 1) {
            return
        }
        if (nextEditText?.text.isNullOrEmpty()) {
            return
        }

        userIsNotMakingChanges = true
        val pulledDigit = nextEditText!!.text[0]
        editText.text.append(pulledDigit)
        if (editText.selectionEnd == editText.text.length) {
            editText.setSelection(editText.selectionEnd - 1)
        }
        nextEditText.text.delete(0, 1)
        nextEditText.setText(nextEditText.text.toString())
        pullDigitFromNextEditText(nextEditTextNum!!)
        userIsNotMakingChanges = false
    }

    private fun getEditText(editTextNum: Int): EditText {
        return when (editTextNum) {
            1 -> binding.phoneEditText1
            2 -> binding.phoneEditText2
            3 -> binding.phoneEditText3
            else -> error("invalid editTextNum")
        }
    }

    private fun getMaxLength(editTextNum: Int): Int {
        return when (editTextNum) {
            1 -> 2
            2 -> 3
            3 -> 4
            else -> error("invalid editTextNum")
        }
    }

    private fun getNextEditTextNum(editTextNum: Int): Int? {
        return when (editTextNum) {
            1 -> 2
            2 -> 3
            3 -> null
            else -> error("invalid editTextNum")
        }
    }

    private fun getNextEditText(editTextNum: Int): EditText? {
        return when (editTextNum) {
            1 -> binding.phoneEditText2
            2 -> binding.phoneEditText3
            3 -> null
            else -> error("invalid editTextNum")
        }
    }
}
