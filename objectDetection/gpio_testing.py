import RPi.GPIO as GPIO
import time

led_pin = 12
light_pin = 18

def main():
    prev_value = None

    GPIO.setmode(GPIO.BOARD)
    GPIO.setup(light_pin, GPIO.IN)
    GPIO.setup(led_pin, GPIO.OUT)

    GPIO.setup(led_pin, GPIO.LOW)
    print("Starting light test")

    try:
        while True:
            value = GPIO.input(light_pin)
            print(value)
            if value != prev_value:
                GPIO.output(led_pin, value == 0)
                prev_value = value
            time.sleep(1)
    finally:
        GPIO.cleanup()

if __name__ == '__main__':
    main()
