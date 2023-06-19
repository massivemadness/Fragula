# Custom ViewPager2

This folder contains a customized version of the `androidx.viewpager2` library. The original source
code was downloaded from the official AndroidX repository and modifications were made to address
`EditText` focus-related issue. (See #17)

## Changes Made

The `EditText` focus issue encountered in the original library has been resolved through
modification in `ScrollEventAdapter` class. The `dispatchSelected` method is fully commented out,
which somehow solves the problem 🤯

## License

The original `androidx.viewpager2` library is licensed under the Apache 2.0 license. The
modifications made to this customized version are also provided under the same license. 
For more information, please refer to the LICENSE file in this repository.