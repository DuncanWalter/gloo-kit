package GlooKit.Utils;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class Matrix {

    /* */
    public static final int IDENTITY = 1;
    public static final int ZERO = 0;


    private float[][] elements;
    private final int SIZE;


    public Matrix(int size) {
        this(size, IDENTITY); // default to the identity matrix if they don't specify a mode
    }

    /* Overloader constructor for specifying the initial mode of the matrix */
    public Matrix(int size, int mode) {

        SIZE = size;
        elements = new float[size][size];

        switch (mode) {
            case IDENTITY:
                this.setIdentity();
                break;
            case ZERO:
                this.setZero();
                break;
            default:
                this.setIdentity(); // automatically default to the identity matrix if something goes wrong
                break;
        }
    }

    /* Copy constructor */
    public Matrix(Matrix source) {

        SIZE = source.SIZE;
        elements = source.elements.clone();
    }


    public void setIdentity() {

        /* Go through each row of the matrix */
        for (int i = 0; i < SIZE; i++) {

            /* Go through each element of the row */
            for (int j = 0; j < SIZE; j++) {
                if (i == j) {
                    elements[i][j] = 1; // if the element is along the diagonal, it is one
                } else {
                    elements[i][j] = 0; // otherwise it is zero
                }
            }
        }
    }

    public void setZero() {
        /* Go through each row of the matrix */
        for (int i = 0; i < SIZE; i++) {

            /* Go through each element of the row */
            for (int j = 0; j < SIZE; j++) {
                elements[i][j] = 0f; // set the element to zero
            }
        }
    }


    /* This call modifies the left matrix */
    public Matrix ADD(Matrix right) {

        if (this.SIZE != right.SIZE) {
            try {
                throw new Exception("ERROR: Matrices could not be added; Matrices are not of the same pointSize!");
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(321); // You done f*cked up, dude!
            }
        }

        /* Go through each row of the matrix */
        for (int i = 0; i < SIZE; i++) {

            /* Go through each element of the row */
            for (int j = 0; j < SIZE; j++) {
                elements[i][j] += right.elements[i][j]; // add the element of the right matrix to the left matrix
            }
        }


        return this; // and return the left matrix
    }

    /* This add call does not modify the left matrix */
    public Matrix add(Matrix right) {

        if (this.SIZE != right.SIZE) {
            try {
                throw new Exception("ERROR: Matrices could not be added; Matrices are not of the same pointSize!");
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(321); // You done f*cked up, dude!
            }
        }

        Matrix result = new Matrix(this.SIZE); // create a new matrix to hold the result

        /* Go through each row of the matrix */
        for (int i = 0; i < SIZE; i++) {

            /* Go through each element of the row */
            for (int j = 0; j < SIZE; j++) {
                result.elements[i][j] = this.elements[i][j] + right.elements[i][j]; // add the element of the right matrix to the left matrix
            }
        }


        return result; // return the resulting matrix
    }


    /* This scale call does modify the matrix */
    public Matrix SCALE(float scalar) {

        /* Go through each row of the matrix */
        for (int i = 0; i < SIZE; i++) {

            /* Go through each element of the row */
            for (int j = 0; j < SIZE; j++) {
                elements[i][j] *= scalar; // multiply the element by the scalar
            }
        }

        return this;
    }

    /* This scale does not modify the matrix */
    public Matrix scale(float scalar) {

        Matrix result = new Matrix(this.SIZE);

        /* Go through each row of the matrix */
        for (int i = 0; i < SIZE; i++) {

            /* Go through each element of the row */
            for (int j = 0; j < SIZE; j++) {
                result.elements[i][j] = this.elements[i][j] * scalar; // multiply the element by the scalar
            }
        }

        return result; // return the resulting matrix
    }


//    /* This multiply does modify the matrix */
//    public Matrix MULTIPLY(Matrix right) {
//
//        if (this.SIZE != right.SIZE) {
//            try {
//                throw new Exception("ERROR: Matrices could not be multiplied; Matrices are not of the same pointSize!");
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.exit(321); // You done f*cked up, dude!
//            }
//        }
//
//        Matrix result = new Matrix(this.SIZE); // create a new matrix to hold the result
//
//        /* Go through each row of the matrix */
//        for(int i = 0; i < SIZE; i++) {
//
//            /* Go through each element of the row */
//            for (int j = 0; j < SIZE; j++) {
//
//                float rowsum = 0; // the sum of the row
//
//                /* Go through each element of the multiplying row */
//                for (int n = 0; n < SIZE; n++) {
//                    rowsum += this.elements[i][n] * right.elements[n][i]; // add the product of the corresponding elements
//                }
//
//                result.elements[i][j] = rowsum;
//            }
//        }
//
//
//        this.elements = result.elements.clone(); // clone the elements from the resulting matrix to this matrix
//
//        return this;
//    }

    /* This multiply does not modify the matrix */
    public Matrix multiply(Matrix right) {

        if (this.SIZE != right.SIZE) {
            try {
                throw new Exception("ERROR: Matrices could not be multiplied; Matrices are not of the same pointSize!");
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(321); // You done f*cked up, dude!
            }
        }

        Matrix result = new Matrix(this.SIZE); // create a new matrix to hold the result

        /* Go through each row of the matrix */
        for(int i = 0; i < SIZE; i++) {

            /* Go through each element of the row */
            for (int j = 0; j < SIZE; j++) {

                float rowsum = 0; // the sum of the row

                /* Go through each element of the multiplying row */
                for (int n = 0; n < SIZE; n++) {
                    rowsum += this.elements[i][n] * right.elements[n][j]; // add the product of the corresponding elements
                }

                result.elements[i][j] = rowsum;
            }
        }

        return result; // return the resulting matrix
    }


    public float determinant() {
        System.out.println("WARNING: determinant() NOT IMPLEMENTED!");
        return 0f;
    }

    public Matrix invert() {
        System.out.println("WARNING: invert() NOT IMPLEMENTED");
        return new Matrix(1); // return just the 1x1 identity matrix
    }

    public void set(int i, int j, float value){
        elements[i][j] = value;
    }

    public String toString() {
        String string = "";

        /* Go through each row of the matrix (except the last one)*/
        for (int i = 0; i < this.SIZE - 1; i++) {

            string += "|";

            /* Go through each element of the row (except the last one) */
            for (int j = 0; j < this.SIZE - 1; j++) {
                string += this.elements[i][j] + "\t";
            }

            /* Do the last element of the row */
            string += this.elements[i][this.SIZE] + " |\n";
        }

        /* Do the last row of the matrix */
        string += "|";

        /* Go through each element of the row (except the last one) */
        for (int j = 0; j < this.SIZE - 1; j++) {
            string += this.elements[this.SIZE][j] + "\t";
        }

            /* Do the last element of the row */
        string += this.elements[this.SIZE][this.SIZE] + " |";

        return string;
    }

    /* Converts this Matrix to a FloatBuffer */
    public FloatBuffer toFloatBuffer() {
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(this.SIZE * this.SIZE); // create a float buffer with (pointSize^2) elements

        /*
        * GLSL takes column-major matrices. That is to say that:
        * MATnxm = n columns by m row
        * THIS IS THE TRANSPOSE FROM THE NORMAL MATHEMATICAL STANDARD!!! In normal math,
        * MATmxn = m rows by n columns
        *
        * To correct this, we need to take the transpose of the matrix as we send it off to the GPU
        * This is as simple as switching [i] and [j] in the this.elements call below
        * */


        /* Go through each row of the matrix */
        for (int i = 0; i < SIZE; i++) {

            /* Go through each element of the row */
            for (int j = 0; j < SIZE; j++) {
                floatBuffer.put(this.elements[j][i]); /* This takes the transpose of the matrix!!! */
            }
        }

        return floatBuffer; // return the float buffer (NOT inverted!)

    }

    /* Put the elements of this Matrix in a parameter FloatBuffer */
    public FloatBuffer storeInFloatBuffer(FloatBuffer floatBuffer) {

        /* Go through each row of the matrix */
        for (int i = 0; i < SIZE; i++) {

            /* Go through each element of the row */
            for (int j = 0; j < SIZE; j++) {
                floatBuffer.put(this.elements[i][j]);
            }
        }

        return floatBuffer; // return the float buffer (NOT inverted!)
    }

}
