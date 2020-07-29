package example

import com.snowbizz.k4rpi.*
import platform.posix.sleep
import platform.posix.system

val k4rpi = K4rpi()

fun main() {
    println("Hello from Example")
    menu()

}

private fun menu() {
    system("clear")
    println()
    println("---------------------------")
    println("1. Pin Mode -> INPUT")
    println("2. Pin Mode -> OUTPUT")
    println("3. Pull Control -> PULL_UP")
    println("4. Pull Control -> PULL_DOWN")
    println("5. Digital Write -> HIGH")
    println("6. Digital Write -> LOW")
    println("7. Digital Read ONCE")
    println("8. Digital Write -> TOGGLE 1s")
    println("9. Digital Write -> TOGGLE FAST")
    println("0. Digital Read LOOP")
    println("---------------------------")
    println("Type test number (0-9) followed by ENTER")
    println()

    var input: Int? = null
    try {
        input = readLine()?.toInt()
    } catch (e: Exception) {
        menu()
    }
    println()
    when (input) {
        1 -> k4rpi.pinMode(GPIO_17, INPUT)
        2 -> k4rpi.pinMode(GPIO_17, OUTPUT)
        3 -> k4rpi.pullControl(GPIO_17, PULL_UP)
        4 -> k4rpi.pullControl(GPIO_17, PULL_DOWN)
        5 -> k4rpi.digitalWrite(GPIO_17, HIGH)
        6 -> k4rpi.digitalWrite(GPIO_17, LOW)
        7 -> println("GPIO " + k4rpi.digitalRead(GPIO_17))
        8 -> while (true) {
            k4rpi.digitalWrite(GPIO_17, HIGH)
            sleep(1)
            k4rpi.digitalWrite(GPIO_17, LOW)
            sleep(1)
        }
        9 -> while (true) {
            k4rpi.speedTest(GPIO_17)
        }
        0 -> while (true) {
            if (k4rpi.digitalRead(GPIO_17) == HIGH)
                println("BUTTON PRESSED")
            sleep(1)
        }

        else -> {
            menu()
        }
    }
    println()
    println("Test finished press ENTER for Menu")
    readLine()
    menu()
}
