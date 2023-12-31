package com.kreative.keycaps;

import static com.kreative.keycaps.StringUtilities.quote;
import static com.kreative.keycaps.StringUtilities.toCodes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyCapLegendItem {
	public static final String TEXT_EJECT = "\u23CF";
	public static final String TEXT_NEXT_TRACK = "\u23ED";
	public static final String TEXT_PREV_TRACK = "\u23EE";
	public static final String TEXT_PLAY_PAUSE = "\u23EF";
	public static final String TEXT_POWER = "\u23FB";
	public static final String TEXT_WINDOWS = "\uF810";
	public static final String TEXT_CONTEXT_MENU = "\uF811";
	public static final String TEXT_OPEN_APPLE = "\uF812";
	public static final String TEXT_SOLID_APPLE = "\uF813";
	public static final String TEXT_ATARI = "\uF820";
	public static final String TEXT_COMMODORE = "\uF821";
	public static final String TEXT_SOLID_AMIGA = "\uF822";
	public static final String TEXT_OPEN_AMIGA = "\uF823";
	public static final String TEXT_LEAP_BACKWARD = "\uF824";
	public static final String TEXT_LEAP_FORWARD = "\uF825";
	public static final String TEXT_GOOGLE_ASSISTANT = "\uF826";
	public static final String TEXT_RASPBERRY_PI = "\uF827";
	public static final String TEXT_VOLUME_MUTE = "\uD83D\uDD07";
	public static final String TEXT_VOLUME_DOWN = "\uD83D\uDD09";
	public static final String TEXT_VOLUME_UP = "\uD83D\uDD0A";
	public static final String TEXT_LISA_LEFT = "\uDBBF\uDC18";
	public static final String TEXT_LISA_UP = "\uDBBF\uDC19";
	public static final String TEXT_LISA_RIGHT = "\uDBBF\uDC1A";
	public static final String TEXT_LISA_DOWN = "\uDBBF\uDC1B";
	public static final String TEXT_NABU_BACKWARD = "\uDBBF\uDCEC";
	public static final String TEXT_NABU_FORWARD = "\uDBBF\uDCED";
	public static final String TEXT_IBM_CAPS_LOCK = "\uDBBF\uDCEE";
	public static final String TEXT_IBM_CHANGE_SIZE = "\uDBBF\uDCEF";
	
	public static final String PATH_EJECT = "M 0.158 0.513 C 0.153 0.5 0.156 0.486 0.165 0.477 l 0.312 -0.312 c 0.013 -0.013 0.033 -0.013 0.046 0 l 0.312 0.312 C 0.844 0.486 0.847 0.5 0.842 0.513 c -0.005 0.012 -0.017 0.02 -0.03 0.02 H 0.188 C 0.175 0.533 0.163 0.524 0.158 0.513 z M 0.812 0.623 H 0.188 c -0.018 0 -0.033 0.015 -0.033 0.033 v 0.156 c 0 0.018 0.015 0.033 0.033 0.033 h 0.624 c 0.018 0 0.033 -0.015 0.033 -0.033 V 0.656 C 0.845 0.638 0.83 0.623 0.812 0.623 z";
	public static final String PATH_NEXT_TRACK = "M 1 0.188 v 0.624 C 1 0.83 0.985 0.844 0.967 0.844 H 0.863 c -0.018 0 -0.032 -0.014 -0.032 -0.032 V 0.188 c 0 -0.018 0.015 -0.032 0.032 -0.032 h 0.104 C 0.985 0.156 1 0.171 1 0.188 z M 0.471 0.166 C 0.462 0.156 0.448 0.153 0.436 0.159 c -0.012 0.005 -0.02 0.017 -0.02 0.03 v 0.624 c 0 0.013 0.008 0.025 0.02 0.03 c 0.004 0.002 0.008 0.002 0.013 0.002 c 0.008 0 0.017 -0.003 0.023 -0.009 l 0.312 -0.312 c 0.013 -0.013 0.013 -0.034 0 -0.046 L 0.471 0.166 z M 0.056 0.166 C 0.046 0.156 0.032 0.153 0.02 0.159 C 0.008 0.164 0 0.175 0 0.188 v 0.624 c 0 0.013 0.008 0.025 0.02 0.03 c 0.004 0.002 0.008 0.002 0.013 0.002 c 0.008 0 0.017 -0.003 0.023 -0.009 l 0.312 -0.312 c 0.013 -0.013 0.013 -0.033 0 -0.046 L 0.056 0.166 z";
	public static final String PATH_PREV_TRACK = "M 0.169 0.188 v 0.623 c 0 0.018 -0.015 0.033 -0.032 0.033 H 0.033 C 0.015 0.844 0 0.83 0 0.812 V 0.188 C 0 0.18 0.003 0.171 0.01 0.165 c 0.006 -0.006 0.014 -0.009 0.023 -0.009 l 0 0 h 0.104 C 0.154 0.156 0.169 0.17 0.169 0.188 z M 0.564 0.158 C 0.552 0.153 0.538 0.156 0.529 0.166 L 0.217 0.477 c -0.013 0.013 -0.013 0.033 0 0.046 l 0.312 0.312 c 0.006 0.006 0.015 0.009 0.023 0.009 c 0.004 0 0.009 -0.001 0.013 -0.002 c 0.012 -0.005 0.02 -0.017 0.02 -0.03 V 0.188 C 0.584 0.175 0.577 0.164 0.564 0.158 z M 0.98 0.158 C 0.968 0.153 0.954 0.156 0.944 0.166 L 0.633 0.477 C 0.62 0.49 0.62 0.51 0.633 0.523 l 0.312 0.312 c 0.006 0.006 0.015 0.01 0.023 0.01 c 0.004 0 0.009 -0.001 0.013 -0.002 C 0.992 0.836 1 0.825 1 0.812 V 0.188 C 1 0.175 0.992 0.164 0.98 0.158 z";
	public static final String PATH_PLAY_PAUSE = "M 0.417 0.475 C 0.424 0.481 0.429 0.49 0.429 0.5 c 0 0.009 -0.004 0.019 -0.011 0.025 L 0.054 0.836 C 0.048 0.841 0.04 0.844 0.033 0.844 c -0.005 0 -0.009 -0.001 -0.014 -0.003 C 0.007 0.836 0 0.824 0 0.812 V 0.188 c 0 -0.013 0.007 -0.024 0.019 -0.03 s 0.025 -0.003 0.035 0.005 L 0.417 0.475 z M 0.656 0.156 H 0.5 c -0.018 0 -0.033 0.015 -0.033 0.033 v 0.623 c 0 0.018 0.015 0.033 0.033 0.033 h 0.156 c 0.018 0 0.033 -0.015 0.033 -0.033 V 0.188 C 0.688 0.17 0.674 0.156 0.656 0.156 z M 0.967 0.156 H 0.812 c -0.018 0 -0.032 0.015 -0.032 0.033 v 0.623 c 0 0.018 0.014 0.033 0.032 0.033 h 0.156 C 0.985 0.844 1 0.83 1 0.812 V 0.188 C 1 0.17 0.985 0.156 0.967 0.156 z";
	public static final String PATH_POWER = "M 0.5 1 C 0.267 1 0.076 0.81 0.076 0.576 c 0 -0.136 0.061 -0.26 0.169 -0.341 c 0.021 -0.015 0.051 -0.011 0.067 0.01 c 0.015 0.021 0.011 0.051 -0.01 0.066 C 0.219 0.374 0.17 0.47 0.17 0.576 c 0 0.181 0.147 0.33 0.33 0.33 s 0.33 -0.149 0.33 -0.33 c 0 -0.106 -0.048 -0.202 -0.132 -0.265 C 0.677 0.296 0.673 0.267 0.689 0.245 c 0.016 -0.02 0.045 -0.025 0.066 -0.01 C 0.863 0.316 0.924 0.44 0.924 0.576 C 0.924 0.81 0.733 1 0.5 1 z M 0.547 0.576 V 0.048 C 0.547 0.021 0.526 0 0.5 0 S 0.453 0.021 0.453 0.048 v 0.528 c 0 0.025 0.021 0.046 0.047 0.046 S 0.547 0.601 0.547 0.576 z";
	public static final String PATH_WINDOWS = "M 0 0 h 0.45 v 0.45 h -0.45 z M 1 0 v 0.45 h -0.45 v -0.45 z M 1 1 h -0.45 v -0.45 h 0.45 z M 0 1 v -0.45 h 0.45 v 0.45 z";
	public static final String PATH_CONTEXT_MENU = "M 0 0 V 1 H 1 V 0 Z M 0.9 0.9 H 0.1 V 0.1 H 0.9 Z M 0.7 0.35 H 0.3 V 0.25 H 0.7 Z M 0.7 0.55 H 0.3 V 0.45 H 0.7 Z M 0.7 0.75 H 0.3 V 0.65 H 0.7 Z";
	public static final String PATH_OPEN_APPLE = "M 0.363 1 C 0.29 1 0.224 0.946 0.162 0.834 c -0.057 -0.1 -0.085 -0.2 -0.085 -0.297 c 0 -0.093 0.024 -0.171 0.07 -0.231 C 0.196 0.242 0.26 0.209 0.336 0.209 c 0.031 0 0.067 0.006 0.108 0.019 c 0.001 0 0.002 0 0.003 0.001 C 0.453 0.181 0.469 0.138 0.495 0.1 c 0.034 -0.049 0.089 -0.083 0.164 -0.099 c 0.021 -0.005 0.044 0.006 0.052 0.027 c 0.004 0.01 0.006 0.02 0.006 0.029 c 0 0.003 0.001 0.007 0.001 0.011 c 0 0.028 -0.007 0.059 -0.02 0.091 C 0.693 0.175 0.685 0.19 0.675 0.205 c 0.054 0 0.102 0.014 0.146 0.043 c 0.025 0.019 0.047 0.04 0.067 0.064 C 0.903 0.332 0.9 0.359 0.881 0.375 C 0.849 0.403 0.834 0.42 0.828 0.43 c -0.02 0.028 -0.029 0.056 -0.029 0.087 c 0 0.035 0.01 0.067 0.03 0.096 c 0.019 0.027 0.039 0.044 0.062 0.051 c 0.011 0.003 0.021 0.012 0.027 0.022 c 0.005 0.01 0.007 0.023 0.003 0.035 C 0.907 0.77 0.882 0.819 0.849 0.87 C 0.792 0.956 0.733 0.998 0.67 0.998 c -0.024 0 -0.053 -0.006 -0.091 -0.02 C 0.545 0.965 0.524 0.963 0.512 0.963 c -0.017 0 -0.038 0.005 -0.06 0.015 C 0.416 0.993 0.388 1 0.363 1 z M 0.336 0.3 c -0.047 0 -0.085 0.02 -0.117 0.061 C 0.185 0.406 0.168 0.465 0.168 0.538 c 0 0.082 0.024 0.167 0.073 0.252 c 0.043 0.078 0.085 0.119 0.122 0.119 c 0.008 0 0.024 -0.002 0.054 -0.015 C 0.483 0.865 0.536 0.865 0.61 0.893 c 0.034 0.012 0.051 0.014 0.06 0.014 c 0.038 0 0.077 -0.047 0.104 -0.087 c 0.02 -0.03 0.036 -0.059 0.047 -0.088 C 0.796 0.716 0.773 0.694 0.754 0.666 c -0.03 -0.044 -0.046 -0.094 -0.046 -0.148 c 0 -0.05 0.015 -0.096 0.044 -0.139 c 0.008 -0.013 0.02 -0.026 0.035 -0.041 C 0.781 0.333 0.775 0.327 0.768 0.322 c -0.042 -0.028 -0.087 -0.038 -0.169 -0.01 C 0.556 0.327 0.527 0.333 0.506 0.333 c -0.011 0 -0.033 -0.002 -0.087 -0.019 C 0.386 0.305 0.358 0.3 0.336 0.3 z M 0.62 0.11 C 0.598 0.121 0.582 0.135 0.57 0.152 C 0.557 0.169 0.548 0.188 0.543 0.209 c 0.009 -0.005 0.02 -0.014 0.032 -0.024 c 0.018 -0.018 0.031 -0.038 0.04 -0.059 C 0.617 0.121 0.619 0.115 0.62 0.11 z";
	public static final String PATH_SOLID_APPLE = "M 0.424 0.249 c 0.041 0.012 0.068 0.018 0.082 0.018 c 0.018 0 0.046 -0.007 0.086 -0.021 s 0.074 -0.021 0.104 -0.021 c 0.048 0 0.091 0.013 0.129 0.039 C 0.845 0.28 0.867 0.3 0.887 0.325 c -0.031 0.026 -0.055 0.05 -0.068 0.071 c -0.026 0.038 -0.04 0.079 -0.04 0.124 c 0 0.05 0.015 0.095 0.042 0.134 c 0.027 0.04 0.06 0.065 0.095 0.076 C 0.901 0.778 0.876 0.828 0.842 0.88 C 0.79 0.959 0.739 0.998 0.687 0.998 c -0.021 0 -0.048 -0.006 -0.084 -0.019 c -0.035 -0.013 -0.065 -0.02 -0.09 -0.02 S 0.459 0.966 0.428 0.98 C 0.396 0.993 0.37 1 0.35 1 C 0.289 1 0.23 0.948 0.172 0.844 C 0.114 0.742 0.084 0.641 0.084 0.542 c 0 -0.092 0.022 -0.167 0.067 -0.225 C 0.196 0.26 0.252 0.231 0.319 0.231 C 0.348 0.231 0.383 0.237 0.424 0.249 z M 0.689 0.014 c 0 0.004 0.001 0.008 0.001 0.012 c 0 0.025 -0.006 0.052 -0.018 0.082 C 0.661 0.137 0.642 0.165 0.617 0.19 C 0.594 0.211 0.573 0.226 0.551 0.233 C 0.538 0.237 0.517 0.241 0.49 0.243 c 0 -0.06 0.016 -0.111 0.046 -0.154 C 0.566 0.046 0.616 0.016 0.686 0 C 0.688 0.005 0.689 0.01 0.689 0.014 z";
	public static final String PATH_COMMODORE = "M 0.571 0.536 h 0.214 L 1 0.75 H 0.571 V 0.536 z M 0.571 0.464 V 0.25 H 1 L 0.786 0.464 H 0.571 z M 0 0.5 C 0 0.223 0.223 0 0.5 0 c 0.025 0 0.048 0.002 0.071 0.006 v 0.217 C 0.548 0.218 0.525 0.214 0.5 0.214 c -0.157 0 -0.286 0.128 -0.286 0.286 S 0.343 0.786 0.5 0.786 c 0.025 0 0.048 -0.004 0.071 -0.009 v 0.217 C 0.548 0.998 0.525 1 0.5 1 C 0.223 1 0 0.777 0 0.5 z";
	public static final String PATH_SOLID_AMIGA = "M 0.654 0.414 l -0.24 0.24 h 0.24 V 0.414 z M -0.039 1 V 0.923 h 0.077 L 0.961 0 v 0.923 h 0.078 V 1 H 0.577 V 0.923 h 0.077 V 0.73 H 0.336 L 0.144 0.923 H 0.23 V 1 H -0.039 z";
	public static final String PATH_OPEN_AMIGA = "M 0.885 0.185 L 0.731 0.338 v 0.585 h 0.154 V 0.185 z M 0.654 0.414 l -0.24 0.24 h 0.24 V 0.414 z M -0.039 1 V 0.923 h 0.077 L 0.961 0 v 0.923 h 0.078 V 1 H 0.577 V 0.923 h 0.077 V 0.73 H 0.336 L 0.144 0.923 h 0.087 V 1 H -0.039 z";
	public static final String PATH_VOLUME_MUTE = "M 0.559 0.188 v 0.624 c 0 0.013 -0.008 0.025 -0.02 0.03 C 0.534 0.844 0.53 0.845 0.526 0.845 c -0.008 0 -0.017 -0.003 -0.023 -0.01 L 0.305 0.637 H 0.11 c -0.018 0 -0.033 -0.015 -0.033 -0.033 V 0.396 c 0 -0.018 0.015 -0.033 0.033 -0.033 h 0.194 l 0.198 -0.198 c 0.009 -0.009 0.023 -0.012 0.036 -0.007 C 0.551 0.163 0.559 0.175 0.559 0.188 z M 0.832 0.5 l 0.081 -0.081 c 0.013 -0.013 0.013 -0.033 0 -0.046 s -0.034 -0.013 -0.046 0 L 0.786 0.454 L 0.705 0.373 c -0.013 -0.013 -0.034 -0.013 -0.046 0 s -0.013 0.033 0 0.046 L 0.74 0.5 L 0.659 0.581 c -0.013 0.013 -0.013 0.033 0 0.046 c 0.006 0.006 0.015 0.01 0.023 0.01 c 0.008 0 0.017 -0.003 0.023 -0.01 l 0.081 -0.081 l 0.081 0.081 c 0.006 0.006 0.015 0.01 0.023 0.01 c 0.009 0 0.017 -0.003 0.023 -0.01 c 0.013 -0.013 0.013 -0.033 0 -0.046 L 0.832 0.5 z";
	public static final String PATH_VOLUME_DOWN = "M 0.637 0.188 v 0.624 c 0 0.013 -0.008 0.025 -0.02 0.03 C 0.612 0.844 0.608 0.844 0.604 0.844 c -0.008 0 -0.017 -0.003 -0.023 -0.009 L 0.382 0.636 H 0.188 c -0.018 0 -0.033 -0.014 -0.033 -0.032 V 0.396 c 0 -0.018 0.015 -0.033 0.033 -0.033 h 0.194 l 0.199 -0.198 C 0.59 0.156 0.604 0.153 0.617 0.158 C 0.628 0.163 0.637 0.175 0.637 0.188 z M 0.784 0.386 C 0.769 0.376 0.749 0.38 0.738 0.396 C 0.729 0.41 0.732 0.431 0.748 0.44 c 0.02 0.014 0.032 0.036 0.032 0.06 S 0.768 0.546 0.748 0.56 c -0.015 0.01 -0.019 0.03 -0.009 0.045 c 0.006 0.009 0.017 0.015 0.027 0.015 c 0.006 0 0.013 -0.002 0.018 -0.006 C 0.822 0.588 0.845 0.545 0.845 0.5 C 0.845 0.454 0.822 0.412 0.784 0.386 z";
	public static final String PATH_VOLUME_UP = "M 0.533 0.188 v 0.624 c 0 0.013 -0.008 0.025 -0.02 0.03 C 0.508 0.844 0.504 0.844 0.5 0.844 c -0.008 0 -0.017 -0.003 -0.023 -0.009 L 0.279 0.637 H 0.084 c -0.018 0 -0.032 -0.015 -0.032 -0.033 V 0.396 c 0 -0.018 0.015 -0.033 0.032 -0.033 h 0.195 l 0.198 -0.198 C 0.486 0.156 0.5 0.153 0.513 0.158 C 0.525 0.163 0.533 0.175 0.533 0.188 z M 0.741 0.5 c 0 -0.046 -0.023 -0.088 -0.061 -0.114 C 0.665 0.376 0.645 0.38 0.634 0.396 C 0.625 0.41 0.628 0.431 0.644 0.44 c 0.02 0.013 0.032 0.036 0.032 0.06 S 0.664 0.546 0.644 0.559 C 0.628 0.569 0.625 0.59 0.635 0.604 c 0.006 0.009 0.017 0.015 0.027 0.015 c 0.006 0 0.013 -0.002 0.018 -0.006 C 0.718 0.588 0.741 0.545 0.741 0.5 z M 0.845 0.5 c 0 -0.081 -0.04 -0.155 -0.107 -0.2 C 0.723 0.29 0.702 0.294 0.692 0.309 c -0.01 0.015 -0.006 0.035 0.009 0.045 C 0.75 0.387 0.779 0.441 0.779 0.5 S 0.75 0.613 0.701 0.646 c -0.015 0.01 -0.019 0.03 -0.009 0.045 C 0.698 0.7 0.709 0.706 0.719 0.706 c 0.006 0 0.013 -0.002 0.018 -0.005 C 0.805 0.655 0.845 0.581 0.845 0.5 z M 0.795 0.213 C 0.78 0.204 0.76 0.208 0.75 0.222 C 0.74 0.237 0.744 0.258 0.759 0.268 C 0.836 0.32 0.883 0.406 0.883 0.5 c 0 0.093 -0.047 0.18 -0.125 0.232 C 0.744 0.742 0.74 0.762 0.75 0.777 c 0.006 0.009 0.017 0.015 0.027 0.015 c 0.006 0 0.013 -0.002 0.018 -0.005 C 0.891 0.722 0.948 0.615 0.948 0.5 S 0.891 0.277 0.795 0.213 z";
	public static final String PATH_LISA_LEFT = "M 1 0.188 v 0.623 C 1 0.83 0.985 0.844 0.967 0.844 H 0.033 C 0.015 0.844 0 0.83 0 0.812 V 0.656 c 0 -0.018 0.015 -0.033 0.033 -0.033 s 0.033 0.015 0.033 0.033 v 0.123 h 0.868 V 0.222 H 0.066 v 0.123 c 0 0.018 -0.015 0.033 -0.033 0.033 S 0 0.362 0 0.344 V 0.188 C 0 0.17 0.015 0.156 0.033 0.156 h 0.935 C 0.985 0.156 1 0.17 1 0.188 z M 0.595 0.688 c 0.003 0 0.006 0.001 0.009 0.001 c 0.007 0 0.014 -0.002 0.02 -0.007 c 0.008 -0.006 0.013 -0.016 0.013 -0.026 V 0.344 c 0 -0.01 -0.005 -0.02 -0.013 -0.026 C 0.616 0.312 0.605 0.31 0.595 0.313 L 0.024 0.468 C 0.01 0.472 0 0.485 0 0.5 s 0.01 0.028 0.024 0.032 L 0.595 0.688 z";
	public static final String PATH_LISA_UP = "M 1 0.188 v 0.623 c 0 0.018 -0.015 0.033 -0.033 0.033 H 0.033 C 0.015 0.845 0 0.829 0 0.812 V 0.188 c 0 -0.018 0.015 -0.033 0.033 -0.033 h 0.312 c 0.018 0 0.033 0.016 0.033 0.033 c 0 0.019 -0.015 0.033 -0.033 0.033 H 0.066 v 0.557 h 0.868 V 0.222 H 0.656 c -0.018 0 -0.033 -0.015 -0.033 -0.033 c 0 -0.018 0.015 -0.033 0.033 -0.033 h 0.312 C 0.985 0.155 1 0.171 1 0.188 z M 0.317 0.675 c 0.006 0.009 0.016 0.014 0.027 0.014 h 0.312 h 0 c 0.018 0 0.033 -0.015 0.033 -0.033 c 0 -0.005 -0.001 -0.011 -0.004 -0.015 L 0.531 0.178 C 0.527 0.165 0.514 0.155 0.5 0.155 s -0.027 0.01 -0.031 0.022 L 0.313 0.646 C 0.31 0.655 0.312 0.666 0.317 0.675 z";
	public static final String PATH_LISA_RIGHT = "M 1 0.188 v 0.156 c 0 0.018 -0.015 0.033 -0.033 0.033 c -0.018 0 -0.032 -0.015 -0.032 -0.033 V 0.222 H 0.065 v 0.557 h 0.869 V 0.656 c 0 -0.018 0.015 -0.033 0.032 -0.033 C 0.985 0.623 1 0.638 1 0.656 v 0.156 C 1 0.83 0.985 0.844 0.967 0.844 H 0.033 C 0.015 0.844 0 0.83 0 0.812 V 0.188 C 0 0.17 0.015 0.156 0.033 0.156 h 0.934 C 0.985 0.156 1 0.17 1 0.188 z M 0.405 0.313 c -0.011 -0.002 -0.021 0 -0.029 0.006 C 0.368 0.324 0.363 0.334 0.363 0.344 v 0.312 c 0 0.01 0.005 0.02 0.013 0.026 c 0.006 0.004 0.013 0.007 0.021 0.007 c 0.003 0 0.006 0 0.009 -0.001 l 0.57 -0.156 C 0.99 0.528 1 0.515 1 0.5 S 0.99 0.472 0.976 0.468 L 0.405 0.313 z";
	public static final String PATH_LISA_DOWN = "M 1 0.188 v 0.623 c 0 0.018 -0.015 0.033 -0.033 0.033 H 0.655 c -0.018 0 -0.032 -0.016 -0.032 -0.033 c 0 -0.019 0.015 -0.033 0.032 -0.033 h 0.279 V 0.222 H 0.065 v 0.557 h 0.279 c 0.018 0 0.032 0.015 0.032 0.033 c 0 0.018 -0.015 0.033 -0.032 0.033 H 0.033 C 0.015 0.845 0 0.829 0 0.812 V 0.188 c 0 -0.018 0.015 -0.033 0.033 -0.033 h 0.934 C 0.985 0.155 1 0.171 1 0.188 z M 0.683 0.325 C 0.676 0.316 0.666 0.312 0.655 0.312 H 0.345 c -0.011 0 -0.021 0.005 -0.027 0.014 C 0.312 0.334 0.31 0.345 0.313 0.354 l 0.155 0.468 C 0.474 0.835 0.486 0.845 0.5 0.845 s 0.026 -0.01 0.031 -0.022 l 0.155 -0.468 C 0.69 0.345 0.688 0.334 0.683 0.325 z";
	public static final String PATH_NABU_BACKWARD = "M 0.436 0.218 v 0.564 c 0 0.011 -0.006 0.021 -0.017 0.026 C 0.415 0.811 0.41 0.812 0.406 0.812 c -0.006 0 -0.012 -0.002 -0.018 -0.006 L 0.012 0.524 C 0.004 0.519 0 0.51 0 0.5 s 0.004 -0.018 0.012 -0.024 l 0.376 -0.282 c 0.009 -0.007 0.021 -0.008 0.031 -0.003 C 0.429 0.197 0.436 0.207 0.436 0.218 z M 0.547 0.188 c -0.016 0 -0.029 0.013 -0.029 0.03 v 0.564 c 0 0.016 0.013 0.029 0.029 0.029 c 0.017 0 0.03 -0.013 0.03 -0.029 V 0.218 C 0.577 0.202 0.563 0.188 0.547 0.188 z M 0.688 0.188 c -0.016 0 -0.029 0.013 -0.029 0.03 v 0.564 c 0 0.016 0.013 0.029 0.029 0.029 c 0.017 0 0.03 -0.013 0.03 -0.029 V 0.218 C 0.718 0.202 0.705 0.188 0.688 0.188 z M 0.83 0.188 c -0.017 0 -0.03 0.013 -0.03 0.03 v 0.564 c 0 0.016 0.013 0.029 0.03 0.029 c 0.016 0 0.029 -0.013 0.029 -0.029 V 0.218 C 0.859 0.202 0.846 0.188 0.83 0.188 z M 0.97 0.188 c -0.016 0 -0.029 0.013 -0.029 0.03 v 0.564 c 0 0.016 0.014 0.029 0.029 0.029 C 0.987 0.812 1 0.799 1 0.783 V 0.218 C 1 0.202 0.987 0.188 0.97 0.188 z";
	public static final String PATH_NABU_FORWARD = "M 1 0.5 C 1 0.51 0.996 0.519 0.988 0.524 L 0.612 0.806 C 0.606 0.81 0.601 0.812 0.594 0.812 c -0.004 0 -0.009 -0.001 -0.013 -0.003 c -0.01 -0.005 -0.017 -0.015 -0.017 -0.026 V 0.218 c 0 -0.011 0.006 -0.021 0.017 -0.026 c 0.01 -0.005 0.022 -0.004 0.031 0.003 l 0.376 0.282 C 0.996 0.482 1 0.491 1 0.5 z M 0.029 0.188 C 0.013 0.188 0 0.202 0 0.218 v 0.564 c 0 0.016 0.013 0.029 0.029 0.029 c 0.017 0 0.03 -0.013 0.03 -0.029 V 0.218 C 0.059 0.202 0.046 0.188 0.029 0.188 z M 0.17 0.188 c -0.016 0 -0.029 0.013 -0.029 0.03 v 0.564 c 0 0.016 0.013 0.029 0.029 0.029 c 0.017 0 0.03 -0.013 0.03 -0.029 V 0.218 C 0.2 0.202 0.187 0.188 0.17 0.188 z M 0.312 0.188 c -0.017 0 -0.03 0.013 -0.03 0.03 v 0.564 c 0 0.016 0.013 0.029 0.03 0.029 c 0.016 0 0.029 -0.013 0.029 -0.029 V 0.218 C 0.341 0.202 0.328 0.188 0.312 0.188 z M 0.453 0.188 c -0.017 0 -0.03 0.013 -0.03 0.03 v 0.564 c 0 0.016 0.013 0.029 0.03 0.029 c 0.016 0 0.029 -0.013 0.029 -0.029 V 0.218 C 0.482 0.202 0.469 0.188 0.453 0.188 z";
	public static final String PATH_IBM_CAPS_LOCK = "M 0.828 0.328 H 0.737 v -0.09 C 0.737 0.106 0.631 0 0.5 0 S 0.262 0.106 0.262 0.238 v 0.09 h -0.09 c -0.022 0 -0.041 0.019 -0.041 0.041 v 0.263 C 0.131 0.834 0.296 1 0.5 1 c 0.203 0 0.369 -0.166 0.369 -0.369 V 0.369 C 0.869 0.346 0.851 0.328 0.828 0.328 z M 0.344 0.238 c 0 -0.086 0.07 -0.156 0.156 -0.156 s 0.155 0.07 0.155 0.156 v 0.09 H 0.344 V 0.238 z M 0.787 0.631 c 0 0.158 -0.129 0.287 -0.287 0.287 S 0.213 0.789 0.213 0.631 V 0.41 h 0.574 V 0.631 z M 0.589 0.53 c 0.012 0.007 0.02 0.021 0.02 0.035 c 0 0.023 -0.019 0.041 -0.041 0.041 c -0.001 0 -0.002 0 -0.002 0 H 0.541 v 0.221 c 0 0.023 -0.019 0.041 -0.041 0.041 c -0.023 0 -0.041 -0.018 -0.041 -0.041 V 0.606 H 0.435 c -0.017 0 -0.032 -0.01 -0.038 -0.025 C 0.39 0.566 0.394 0.548 0.405 0.537 l 0.065 -0.065 c 0.004 -0.004 0.008 -0.007 0.014 -0.009 C 0.488 0.46 0.493 0.459 0.498 0.459 c 0.001 0 0.001 0 0.002 0 h 0.001 c 0.005 0 0.01 0.001 0.015 0.003 C 0.52 0.464 0.523 0.467 0.527 0.47 c 0 0 0.001 0 0.002 0.001 L 0.589 0.53 z";
	public static final String PATH_IBM_CHANGE_SIZE = "M 0.549 0.484 c 0.008 0.009 0.008 0.022 0 0.031 L 0.481 0.584 C 0.477 0.588 0.471 0.59 0.466 0.59 c -0.003 0 -0.005 -0.001 -0.008 -0.002 C 0.45 0.585 0.444 0.577 0.444 0.568 V 0.521 H 0.351 v 0.047 c 0 0.009 -0.005 0.017 -0.013 0.02 C 0.334 0.589 0.332 0.59 0.329 0.59 c -0.005 0 -0.011 -0.002 -0.015 -0.006 L 0.246 0.516 c -0.008 -0.009 -0.008 -0.022 0 -0.031 l 0.068 -0.068 C 0.32 0.41 0.329 0.408 0.337 0.412 c 0.008 0.003 0.013 0.011 0.013 0.02 v 0.047 h 0.094 V 0.432 c 0 -0.009 0.005 -0.017 0.013 -0.02 C 0.466 0.408 0.475 0.41 0.481 0.416 L 0.549 0.484 z M 0.18 0.432 v 0.137 c 0 0.012 -0.01 0.021 -0.021 0.021 H 0.021 C 0.01 0.59 0 0.58 0 0.568 V 0.432 C 0 0.42 0.01 0.41 0.021 0.41 h 0.137 C 0.17 0.41 0.18 0.42 0.18 0.432 z M 0.137 0.453 H 0.043 v 0.094 h 0.094 V 0.453 z M 1 0.329 v 0.342 c 0 0.012 -0.01 0.021 -0.021 0.021 H 0.637 c -0.012 0 -0.021 -0.01 -0.021 -0.021 V 0.329 c 0 -0.012 0.01 -0.021 0.021 -0.021 h 0.342 C 0.99 0.308 1 0.317 1 0.329 z M 0.957 0.351 H 0.658 v 0.299 h 0.299 V 0.351 z";
	
	public static KeyCapLegendItem parse(KeyCapParser p) {
		if (p.hasNextID()) return text(p.next());
		if (p.hasNextQuote('\'')) return text(p.nextQuote('\''));
		if (p.hasNextQuote('\"')) return text(p.nextQuote('\"'));
		if (p.hasNextQuote('`')) return path(p.nextQuote('`'));
		if (p.hasNextCoded()) return text(p.nextCoded());
		throw p.expected("legend item");
	}
	
	public static KeyCapLegendItem parse(String s, boolean fallback) {
		try {
			KeyCapParser p = new KeyCapParser(s);
			KeyCapLegendItem item = parse(p);
			p.expectEnd();
			return item;
		} catch (IllegalArgumentException e) {
			return (fallback ? text(s) : null);
		}
	}
	
	private static final Pattern PATH_TAG = Pattern.compile("<path\\s+d\\s*=\\s*(\'[^\']*\'|\"[^\"]*\")\\s*/?>");
	
	public static KeyCapLegendItem text(String text) {
		if (text == null || text.length() == 0) return null;
		Matcher m = PATH_TAG.matcher(text);
		if (m.matches()) {
			String path = m.group(1).substring(1, m.group(1).length() - 1).trim();
			return (path.length() == 0) ? null : new KeyCapLegendItem(text, null, null, path);
		}
		int cpc = text.codePointCount(0, text.length());
		if (cpc != 1) return new KeyCapLegendItem(text, null, text, null);
		switch (text.codePointAt(0)) {
			// These characters are substituted with ones that look nicer.
			case '-': return new KeyCapLegendItem(text, null, "\u2212", null);
			// These private use characters are automatically converted to paths.
			case 0x23CF: return new KeyCapLegendItem(text, null, null, PATH_EJECT);
			case 0x23ED: return new KeyCapLegendItem(text, null, null, PATH_NEXT_TRACK);
			case 0x23EE: return new KeyCapLegendItem(text, null, null, PATH_PREV_TRACK);
			case 0x23EF: return new KeyCapLegendItem(text, null, null, PATH_PLAY_PAUSE);
			case 0x23FB: return new KeyCapLegendItem(text, null, null, PATH_POWER);
			case 0xF810: case 0xFFCE0: return new KeyCapLegendItem(text, null, null, PATH_WINDOWS);
			case 0xF811: case 0xFFCE1: return new KeyCapLegendItem(text, null, null, PATH_CONTEXT_MENU);
			case 0xF812: case 0xFFCE2: return new KeyCapLegendItem(text, null, null, PATH_OPEN_APPLE);
			case 0xF813: case 0xFFCE3: return new KeyCapLegendItem(text, null, null, PATH_SOLID_APPLE);
			case 0xF821: case 0xFFCE5: return new KeyCapLegendItem(text, null, null, PATH_COMMODORE);
			case 0xF822: case 0xFFCE6: return new KeyCapLegendItem(text, null, null, PATH_SOLID_AMIGA);
			case 0xF823: case 0xFFCE7: return new KeyCapLegendItem(text, null, null, PATH_OPEN_AMIGA);
			case 0x1F507: return new KeyCapLegendItem(text, null, null, PATH_VOLUME_MUTE);
			case 0x1F509: return new KeyCapLegendItem(text, null, null, PATH_VOLUME_DOWN);
			case 0x1F50A: return new KeyCapLegendItem(text, null, null, PATH_VOLUME_UP);
			case 0xFFC18: return new KeyCapLegendItem(text, null, null, PATH_LISA_LEFT);
			case 0xFFC19: return new KeyCapLegendItem(text, null, null, PATH_LISA_UP);
			case 0xFFC1A: return new KeyCapLegendItem(text, null, null, PATH_LISA_RIGHT);
			case 0xFFC1B: return new KeyCapLegendItem(text, null, null, PATH_LISA_DOWN);
			case 0xFFCEC: return new KeyCapLegendItem(text, null, null, PATH_NABU_BACKWARD);
			case 0xFFCED: return new KeyCapLegendItem(text, null, null, PATH_NABU_FORWARD);
			case 0xFFCEE: return new KeyCapLegendItem(text, null, null, PATH_IBM_CAPS_LOCK);
			case 0xFFCEF: return new KeyCapLegendItem(text, null, null, PATH_IBM_CHANGE_SIZE);
			// All other characters are taken verbatim.
			default: return new KeyCapLegendItem(text, null, text, null);
		}
	}
	
	public static KeyCapLegendItem path(String path) {
		if (path == null || path.trim().length() == 0) return null;
		return new KeyCapLegendItem(null, path, null, path.trim());
	}
	
	private final PropertyMap props;
	private final String rawText;
	private final String rawPath;
	private final String text;
	private final String path;
	
	private KeyCapLegendItem(String rawText, String rawPath, String text, String path) {
		this.props = new PropertyMap();
		this.rawText = rawText;
		this.rawPath = rawPath;
		this.text = text;
		this.path = path;
	}
	
	public PropertyMap getPropertyMap() { return this.props; }
	public String getRawText() { return this.rawText; }
	public String getRawPath() { return this.rawPath; }
	public String getText() { return this.text; }
	public String getPath() { return this.path; }
	
	public boolean isImpliedLetterOrSymbol() {
		if (text == null || text.length() == 0) return false;
		int cpc = text.codePointCount(0, text.length());
		if (cpc != 1) return false;
		int ch = text.codePointAt(0);
		// These ranges typically label function or modifier keys, not letter or symbol keys.
		if (ch >= 0x2190 && ch <= 0x21FF) return false; // Arrows
		if (ch >= 0x2300 && ch <= 0x23FF) return false; // Miscellaneous Technical
		if (ch >= 0xF810 && ch <= 0xF813) return false; // Linux PUA keyboard symbols
		if (ch >= 0xF820 && ch <= 0xF827) return false; // Kreative PUA keyboard symbols
		if (ch >= 0xFFC18 && ch <= 0xFFC1B) return false; // SLC Appendix keyboard symbols
		if (ch >= 0xFFCE0 && ch <= 0xFFCEF) return false; // SLC Appendix keyboard symbols
		return true;
	}
	
	public String toString() {
		return format(rawText, rawPath);
	}
	
	private static String format(String text, String path) {
		if (path != null && path.length() > 0) return quote(path, '`');
		if (text == null || text.length() == 0) return "$";
		if (text.codePointCount(0, text.length()) == 1) {
			int ch = text.codePointAt(0);
			if (ch < 0x20 || ch >= 0x7F) {
				return toCodes(text);
			}
		}
		if (!text.contains("\'")) return quote(text, '\'');
		if (!text.contains("\"")) return quote(text, '\"');
		return quote(text, '\'');
	}
}
