# Fractal Animator

**Fractal Animator** is a Java-based GUI tool for previewing and animating fractal zoom image sequences exported from tools like Kalles Fraktaler and Fractal Zoomer. It supports smooth playback with multiple interpolation models and includes various preset styles for displaying zoom text, such as "KF style", "FX style", and analog odometer-style readouts.

## ✨ Features

* 📂 **Support for fractal image sequences**

  * Compatible with Kalles Fraktaler and Fractal Zoomer output formats
  * Loads folder of sequentially named frames

* 🧮 **Multiple speed interpolation algorithms**

  * Linear
  * Ease-in / ease-out (acceleration & deceleration)
  * Derivative-based acceleration models (for smoother curve control)

* 🔠 **Zoom text overlay with preset styles**

  * `KF style` (Kalles Fraktaler-style formatting)
  * `FX style` (Fractal eXtreme-style formatting)
  * `Odometer style` (mechanical, analog digit rollover)
  * And so on.

* 📏 **High-resolution support**

  * Scales well with large image sequences
  * Optimized rendering pipeline

## 🛠️ Requirements

* Java 11 or higher
* No external dependencies

## 🚀 How to Use

1. Launch the application:

   ```bash
   java -jar FractalAnimator.jar
   ```

2. Select your image sequence folder.

3. Choose an interpolation method and a zoom text display style from the GUI.

4. Click “Render” to begin export.

## ⚠️ Disclaimer

This project is **not affiliated with** Kalles Fraktaler, Fractal Zoomer, or any of their developers. All product names and trademarks are property of their respective owners.
