Programming Assignment 7: Seam Carving


/* *****************************************************************************
 *  Describe concisely your algorithm to find a horizontal (or vertical)
 *  seam.
 **************************************************************************** */
We use a dynamic programming approach to find a vertical seam. We have a
energyTo array where each of the entries store the total energy of the smallest
seam up to that point (ex. the first row is just the energies of the
corresponding row and column, but the rest of the entries are additive).
At every entry (excluding those in the first row), we check its parents'
energyTo entries (either 2 or 3 parents depending on its position) and pick
the lowest energy seam leading up to that point. We store that parent in the
pixelAbove array where the entry is (but with row - 1 because the first row
does not have parents).

Lastly, we loop through the last entry of energyTo to find the ending column
index of the minimum energy seam. Using that, we index to go back through the
pixelAbove array to put together the seam array to remove.


/* *****************************************************************************
 *  Describe what makes an image suitable to the seam-carving approach
 *  (in terms of preserving the content and structure of the original
 *  image, without introducing visual artifacts). Describe an image that
 *  would not work well.
 **************************************************************************** */
An image that is suitable to the seam carving approach are those with a very
central figure surrounded by a lot of low energy pixels (for example, the
chameleon image). Here, we will be able to remove many seams around the
central figure that are not important.
The main content of the image in this case is the central figure, so removing
seams from background will barely change the content and structure of the original
image.

An image that would not work well would be an image where all the pixels are
important and very different from each other. For example, if everything in
the image is in high contrast (maybe there are a lot of different objects and
no central figure). Shrinking this image will cause us to lose a lot of
information contained in the original image.

/* *****************************************************************************
 *  Perform computational experiments to estimate the running time to reduce
 *  a W-by-H image by one column and one row (i.e., one call each to
 *  findVerticalSeam(), removeVerticalSeam(), findHorizontalSeam(), and
 *  removeHorizontalSeam()). Use a "doubling" hypothesis, where you
 *  successively increase either W or H by a constant multiplicative
 *  factor (not necessarily 2).
 *
 *  To do so, fill in the two tables below. Each table must have 5-10
 *  data points, ranging in time from around 0.25 seconds for the smallest
 *  data point to around 30 seconds for the largest one.
 **************************************************************************** */

(keep W constant)
 W = 2000
 multiplicative factor (for H) = 2

 H           time (seconds)      ratio       log ratio
------------------------------------------------------
500              0.28              -             -
1000             0.48             1.70          0.77
2000             0.81             1.67          0.74
4000             1.64             2.04          1.03
8000             3.18             1.94          0.96
16000            6.37             2.00          1.00
32000           13.52             2.12          1.09
64000           27.61             2.04          1.03


(keep H constant)
 H = 2000
 multiplicative factor (for W) = 2

 W           time (seconds)      ratio       log ratio
------------------------------------------------------
500              0.29              -             -
1000             0.46             1.59          0.67
2000             0.79             1.72          0.78
4000             1.63             2.06          1.04
8000             3.21             1.97          0.98
16000            6.37             1.98          0.99
32000           12.07             1.89          0.92
64000           26.20             2.17          1.12


/* *****************************************************************************
 *  Using the empirical data from the above two tables, give a formula
 *  (using tilde notation) for the running time (in seconds) as a function
 *  of both W and H, such as
 *
 *       ~ 5.3*10^-8 * W^5.1 * H^1.5
 *
 *  Briefly explain how you determined the formula for the running time.
 *  Recall that with tilde notation, you include both the coefficient
 *  and exponents of the leading term (but not lower-order terms).
 *  Round each coefficient and exponent to two significant digits.
 **************************************************************************** */


Running time (in seconds) to find and remove one horizontal seam and one
vertical seam, as a function of both W and H:


    ~ 9.3 * 10^(-8) * W^1.0 * H^1.1
       _______________________________________

Assuming that the running time satisfies T(n) = a x n^b, we can see from the
log ratio data for height and width that b seems to converge to ≈1 and ≈1.1,
respectively. With that established, we will caculate a (using our data) for both
width and height.

We can assume the form of the equation T = a * W^b * H*b
We can approximate b as 1 for the width, and b as 1.1 for the height's exponent
(which we stated above).

With that established, we can plug in an example of
T(n) and W and H to calculate a.

Using W = 16000, and H = 2000 and plugging in 6.37 seconds for T, we get
6.37 = a (16000)^1 * (2000)^1.1, so we calculate a through division then round it
to two significant digits, which gives us a = 9.3 * 10^(-8).

/* *****************************************************************************
 *  Known bugs / limitations.
 **************************************************************************** */

n/a


/* *****************************************************************************
 *  Describe any serious problems you encountered.
 **************************************************************************** */

n/a


/* *****************************************************************************
 *  List any other comments here. Feel free to provide any feedback
 *  on how much you learned from doing the assignment, and whether
 *  you enjoyed doing it.
 **************************************************************************** */

 It was a cool assignment! Just runs very slowly :(
