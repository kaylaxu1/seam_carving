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
