import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

/* SeamCarver object to find and remove lowest energy horizontal/vertical seams
reference: https://www.cs.princeton.edu/courses/archive/fall23/cos226/assignments/seam/specification.php */
public class SeamCarver {
    // create a defensive copy of the picture
    private Picture copy;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("Picture cannot be null!");
        }
        copy = new Picture(picture);
    }

    // current picture
    public Picture picture() {
        return new Picture(copy); // so that client cannot mutate picture
    }

    // width of current picture
    public int width() {
        return copy.width();
    }

    // height of current picture
    public int height() {
        return copy.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x > width() - 1 || y > height() - 1 || x < 0 || y < 0) {
            System.out.println("width:" + x);
            System.out.println("height:" + y);
            throw new IllegalArgumentException("Coordinates out of range!");
        }

        // check for corner and border pixels; if found, reassign
        // coordinates that wrap around
        int rightX = x + 1;
        int leftX = x - 1;
        int topY = y - 1;
        int bottomY = y + 1;

        // wrap around if it's on left border
        if (x == 0) {
            leftX = width() - 1;
        }
        // wrap around if it's on right border
        if (x == width() - 1) {
            rightX = 0;
        }
        // wrap around if it's on top border
        if (y == 0) {
            topY = height() - 1;
        }
        // wrap around if it's on bottom border
        if (y == height() - 1) {
            bottomY = 0;
        }

        int leftColor = copy.getRGB(leftX, y); // left pixel
        int rightColor = copy.getRGB(rightX, y); // right pixel
        int topColor = copy.getRGB(x, topY); // top pixel
        int bottomColor = copy.getRGB(x, bottomY); // bottom pixel

        // calculate the square of the x-gradient
        double xGrad = grad(leftColor, rightColor);
        // calculate the square of the y-gradient
        double yGrad = grad(topColor, bottomColor);
        // calculate the dual-gradient energy function
        return Math.sqrt(xGrad + yGrad);
    }

    // simplifying number of calls to get()
    private double grad(int firstRGB, int secondRGB) {
        int rLeft = (firstRGB >> 16) & 0xFF; // check left red value
        int gLeft = (firstRGB >> 8) & 0xFF; // check left green value
        int bLeft = (firstRGB) & 0xFF; // check left blue value
        int rRight = (secondRGB >> 16) & 0xFF; // check right red value
        int gRight = (secondRGB >> 8) & 0xFF; // check right green value
        int bRight = (secondRGB) & 0xFF; // check right blue value

        // calculate sum within the sqrt
        double grad = Math.pow(rRight - rLeft, 2) + Math.pow(gRight - gLeft, 2)
                + Math.pow(bRight - bLeft, 2);
        return grad;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        transpose();
        int[] horizontalSeam = findVerticalSeam();
        transpose();
        return horizontalSeam;
    }

    // transpose picture
    private void transpose() {
        // change dimensions
        Picture retPicture = new Picture(height(), width());

        // use col, row of original picture for new picture
        for (int row = 0; row < width(); row++) {
            for (int col = 0; col < height(); col++) {
                retPicture.setRGB(col, row, copy.getRGB(row, col));
            }
        }
        copy = retPicture;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        // store pointer to previous pixel in seam
        int[][] pixelAbove = new int[height() - 1][width()];

        // store weight of seam up to that pixel
        double[][] energyTo = new double[height()][width()];

        for (int row = 0; row < height(); row++) {
            for (int col = 0; col < width(); col++) {
                if (row == 0) {
                    // set energyTo for the first row equal to itself
                    energyTo[row][col] = energy(col, row);
                    continue;
                }

                // if leftmost column, we check top and top right neighbors
                if (col == 0) {
                    // corner case when there is only one column
                    if (width() == 1) {
                        pixelAbove[row - 1][col] = col;
                        break;
                    }

                    // increase energy of current pixel by minimum of 2 parents
                    if (energyTo[row - 1][col + 1] < energyTo[row - 1][col]) {
                        energyTo[row][col] = energy(col, row)
                                + energyTo[row - 1][col + 1];

                        pixelAbove[row - 1][col] = col + 1;

                    }
                    else {
                        energyTo[row][col] = energy(col, row)
                                + energyTo[row - 1][col];

                        pixelAbove[row - 1][col] = col;
                    }

                }

                // if rightmost column, just check top and top left neighbors
                else if (col == width() - 1) {
                    // increase energy of current pixel by minimum of 2 parents
                    if (energyTo[row - 1][col - 1] < energyTo[row - 1][col]) {
                        energyTo[row][col] = energy(col, row)
                                + energyTo[row - 1][col - 1];
                        pixelAbove[row - 1][col] = col - 1;
                    }
                    else {
                        energyTo[row][col] = energy(col, row)
                                + energyTo[row - 1][col];
                        pixelAbove[row - 1][col] = col;
                    }

                }

                // if the pixel is not on the border, check all three parents
                else {
                    // find minimum running energy out of the three parents
                    double minEnergy = Double.POSITIVE_INFINITY;

                    // keeps track of the parent with minimum running energy
                    int minPixel = Integer.MAX_VALUE;

                    // check pixel's three parents
                    for (int i = -1; i <= 1; i++) {
                        double currEnergy = energyTo[row - 1][col - i];
                        if (currEnergy < minEnergy) {
                            minEnergy = currEnergy; // update minimum energy
                            minPixel = col - i;
                        }
                    }
                    // update running energy of current pixel
                    energyTo[row][col] = energy(col, row) + minEnergy;
                    // store the column to remove in the previous row
                    pixelAbove[row - 1][col] = minPixel;
                }
            }
        }

        // find minimum seam by checking last row of pixel energies
        double endEnergy = Double.POSITIVE_INFINITY;
        int endIndex = Integer.MAX_VALUE;

        for (int j = 0; j < width(); j++) {
            if (energyTo[height() - 1][j] < endEnergy) {
                endEnergy = energyTo[height() - 1][j];
                endIndex = j;
            }
        }

        // corner case for one row
        if (height() == 1) {
            int[] oneValue = new int[] { endIndex };
            return oneValue;
        }

        int[] seamFound = new int[height()];
        seamFound[height() - 1] = endIndex;

        // loop backwards to get the seam
        int r = height() - 2; // last row of pixelAbove (remove endIndex col)

        // column # to remove in previous row
        int colPointer = endIndex;
        while (r >= 0) {
            int colRemove = pixelAbove[r][colPointer];
            seamFound[r] = colRemove;
            colPointer = colRemove;
            r -= 1; // decrement row
        }
        return seamFound;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        transpose(); // flip image
        removeVerticalSeam(seam);
        transpose();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("Seam cannot be null!");
        }
        checkSeam(seam); // checks if two adjacent entries differ by more than 1
        pictureCheck(); // doesn't remove if the picture is 1D

        if (seam.length != height()) {
            throw new IllegalArgumentException("Seam longer than allowed!");
        }

        Picture newPic = new Picture(width() - 1, height());
        for (int row = 0; row < height(); row++) {
            int picCol = 0;
            for (int col = 0; col < width() - 1; col++) {
                // skip entries in seam
                if (col == seam[row]) picCol++;
                newPic.setRGB(col, row, copy.getRGB(picCol + col, row));
            }
        }
        copy = newPic;
    }

    // throws exception if the picture is 1D (length 1)
    private void pictureCheck() {
        if (width() <= 1) {
            throw new IllegalArgumentException("Cannot remove!");
        }
    }

    // throws exception if seam is invalid
    private void checkSeam(int[] seam) {
        for (int i = 0; i < seam.length; i++) {
            // check if all entries are valid

            if (seam[i] >= width() || seam[i] < 0) {
                throw new IllegalArgumentException("Seam is invalid");
            }

            // check if entries differ by at most 1
            if (i > 0 && Math.abs(seam[i - 1] - seam[i]) > 1) {
                throw new IllegalArgumentException("Seam is invalid");
            }
        }
    }

    //  unit testing (required)
    public static void main(String[] args) {
        // test all methods
        SeamCarver sC = new SeamCarver(new Picture(args[0]));
        StdOut.println(sC.picture());
        StdOut.println("Picture width: " + sC.width());
        StdOut.println("Picture height " + sC.height());

        for (int row = 0; row < sC.height(); row++) {
            for (int col = 0; col < sC.width(); col++) {
                StdOut.println("Energy: " + sC.energy(col, row));
            }
            StdOut.println();
        }

        int[] hSeam = sC.findHorizontalSeam();
        StdOut.println("Horizontal seam removed: ");
        for (int i = 0; i < hSeam.length; i++)
            StdOut.print(hSeam[i] + " ");

        sC.removeHorizontalSeam(hSeam);

        StdOut.println("New width after removing hSeam: " + sC.width());
        StdOut.println("New height after removing hSeam:" + sC.height());

        int[] vSeam = sC.findVerticalSeam();
        StdOut.println("Vertical seam removed: ");
        for (int i = 0; i < vSeam.length; i++)
            StdOut.println(vSeam[i] + " ");

        sC.removeVerticalSeam(vSeam);

        StdOut.println("New width after removing vSeam: " + sC.width());
        StdOut.println("New height after removing vSeam: " + sC.height());

        // SeamCarver sC2 = new SeamCarver(new Picture("12x10.png"));
        // int[] invalid = new int[] { -1, 0, 1, 1, 2, 1, 0, 1, 0, 0 };
        // sC2.removeVerticalSeam(invalid); // should throw exception

        SeamCarver timing = new SeamCarver(SCUtility.randomPicture(2000, 40000));
        Stopwatch stopwatch = new Stopwatch();
        int[] vS = timing.findVerticalSeam();
        timing.removeVerticalSeam(vS);
        int[] hS = timing.findHorizontalSeam();
        timing.removeHorizontalSeam(hS);
        StdOut.println("Elapsed Time: " + stopwatch.elapsedTime());
        StdOut.println("Width: " + timing.width());
        StdOut.println("Height: " + timing.height());

    }

}
