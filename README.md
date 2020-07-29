# Kotlin Native for Raspberry Pi (K4RPi)
## Introduction
Simple Kotlin Native library to control GPIO on Raspberry Pi through the **mmap** call.

Covered GPIO functionalities:
- PinMode (INPUT/OUTPUT)
- PullControl (PULL_UP/PULL_DOWN)
- DigitalWrite (LOW/HIGH)
- DigitalRead ()

The main purpose of creating this library was to check the maximum speed of switching GPIO state in Kotlin Native.
At a later stage I will compare this speed with other methods available in Kotlin Native like wiringPi or bcm2835 libraries wrappers.

***I am not responsible for any damage caused to your software, equipment, or anything else. Proceed at your own risk.**

##Examples
Blink Led on GPIO_17
```kotlin
val k4rpi = K4rpi()
k4rpi.pinMode(17, OUTPUT)

while (true) {
    k4rpi.digitalWrite(17, HIGH)
    sleep(1)
    k4rpi.digitalWrite(17, LOW)
    sleep(1)
}
```

Check if Button on GPIO_17 is pressed
```kotlin
val k4rpi = K4rpi()
k4rpi.pinMode(17, INPUT)
k4rpi.pullControl(17, PULL_DOWN)

while (true) {
    if(k4rpi.digitalRead(17) == HIGH)
        println("BUTTON PRESSED")
    sleep(1)
}
```

## GPIO speed
The fastest available method to change the state of the GPIO pin is to directly change the value of the pointer from mmap call: 
```
fun speedTest(pin: Int) {
    while (true) {
        gpioPointer[GPCLR / 4 + pin / 32] = (1 shl (pin % 32))
        gpioPointer[GPSET / 4 + pin / 32] = (1 shl (pin % 32))
    }
}
```
**The speed is about 29.5 MHz**

## Enviroment Configurations
- Raspberry Pi 3B+ Rev 1.2, Raspberry Pi Zero-W Rev 1.1
- Debian Buster with Raspberry Pi Desktop (release: '2020-02-12', kernel: '4.19')

## Grade SSH plugin
Transfering executable file to Raspberry Pi can be automated by dedicated gradle task.\
In order to use it provide SSH details in build.gradle file: host, username, password and uncomment this line:
```
build.dependsOn upload
```  

## References 
Kotlin/Native interoperability:
https://kotlinlang.org/docs/reference/native/c_interop.html

Gradle SSH plugin:
https://gradle-ssh-plugin.github.io/

## Future Plans
- GPIO Speed test for different libraries (mmap, wiringPi and bcm2835 lib) in Kotlin and C language.
- Add Support for PWM, I2C, SPI, UART, etc.

