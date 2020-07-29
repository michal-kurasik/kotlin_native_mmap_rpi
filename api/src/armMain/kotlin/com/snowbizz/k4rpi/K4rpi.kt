package com.snowbizz.k4rpi

import kotlinx.cinterop.*
import platform.posix.*

typealias PinState = Int
typealias PinMode = Int
typealias PullMode = Int
typealias Pin = Int

const val GPIO_17: Pin = 17

const val LOW: PinState = 0
const val HIGH: PinState = 1

const val INPUT: PinMode = 0
const val OUTPUT: PinMode = 1

const val PULL_OFF = 0x00
const val PULL_DOWN = 0x01
const val PULL_UP = 0x02

class K4rpi {

    private val GPIO = 0x3F200000
    private val GPFSEL = 0x0000
    private val GPSET = 0x001C
    private val GPCLR = 0x0028
    private val GPLVL = 0x0034
    private val GPPUD = 0x0094
    private val GPPUDCLK = 0x0098

    private lateinit var gpioPointer: CPointer<IntVar>

    init {
        memScoped {
            val fd = open("/dev/mem", O_RDWR or O_SYNC)
            gpioPointer =
                mmap(null, getpagesize().toUInt(), PROT_READ or PROT_WRITE, MAP_SHARED, fd, GPIO)!!.reinterpret()
            close(fd)
        }
    }

    fun pinMode(pin: Pin, mode: PinMode) {
        val registerIndex: Int = GPFSEL / 4 + pin / 10
        val bitIndex: Int = pin % 10 * 3
        if (mode == INPUT)
            gpioPointer[registerIndex] = gpioPointer[registerIndex] and (7 shl bitIndex).inv()
        else if (mode == OUTPUT)
            gpioPointer[registerIndex] = gpioPointer[registerIndex] and (7 shl bitIndex).inv() or (1 shl bitIndex)
    }

    fun digitalWrite(pin: Pin, state: PinState) {
        gpioPointer[(if (state == LOW) GPCLR else GPSET) / 4 + pin / 32] = (1 shl (pin % 32))
    }

    fun digitalRead(pin: Pin): PinState {
        return if (gpioPointer[GPLVL / 4 + pin / 32] and (1 shl (pin % 32)) == 0) LOW else HIGH
    }

    fun pullControl(pin: Pin, mode: PullMode) {
        gpioPointer[GPPUD / 4] = mode and 3
        usleep(10)
        gpioPointer[GPPUDCLK / 4 + pin / 32] = 1 shl (pin % 32)
        usleep(10)
        gpioPointer[GPPUD / 4] = 0
        gpioPointer[GPPUDCLK / 4 + pin / 32] = 0
    }

    fun speedTest(pin: Pin) {
        while (true) {
            gpioPointer[GPCLR / 4 + pin / 32] = (1 shl (pin % 32))
            gpioPointer[GPSET / 4 + pin / 32] = (1 shl (pin % 32))
        }
    }
}