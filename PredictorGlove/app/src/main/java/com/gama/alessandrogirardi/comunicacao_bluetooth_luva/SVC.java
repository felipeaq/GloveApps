package com.gama.alessandrogirardi.comunicacao_bluetooth_luva;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class SVC {

    private enum Kernel {LINEAR, POLY, RBF, SIGMOID}

    private int nClasses;
    private int nRows;
    private int[] classes;
    private static double[][] vectors;
    private static double[][] coefficients;
    private static double[] intercepts;
    private static int[] weights;
    private Kernel kernel;
    private double gamma;
    private double coef0;
    private double degree;
    private static SVC ME;

    private SVC() {
    }

    private static SVC getInstance(int nClasses, int nRows, double[][] vectors, double[][] coefficients, double[] intercepts, int[] weights, String kernel, double gamma, double coef0, double degree) {
        if (ME == null) {
            ME = new SVC();
        }

        ME.nClasses = nClasses;
        ME.classes = new int[nClasses];
        for (int i = 0; i < nClasses; i++) {
            ME.classes[i] = i;
        }
        ME.nRows = nRows;

        ME.vectors = vectors;
        ME.coefficients = coefficients;
        ME.intercepts = intercepts;
        ME.weights = weights;

        ME.kernel = Kernel.valueOf(kernel.toUpperCase());
        ME.gamma = gamma;
        ME.coef0 = coef0;
        ME.degree = degree;
        return ME;
    }

    private int predict(double[] features) {

        double[] kernels = new double[vectors.length];
        double kernel;
        switch (this.kernel) {
            case LINEAR:
                // <x,x'>
                for (int i = 0; i < vectors.length; i++) {
                    kernel = 0.;
                    for (int j = 0; j < vectors[i].length; j++) {
                        kernel += vectors[i][j] * features[j];
                    }
                    kernels[i] = kernel;
                }
                break;
            case POLY:
                // (y<x,x'>+r)^d
                for (int i = 0; i < vectors.length; i++) {
                    kernel = 0.;
                    for (int j = 0; j < vectors[i].length; j++) {
                        kernel += vectors[i][j] * features[j];
                    }
                    kernels[i] = Math.pow((this.gamma * kernel) + this.coef0, this.degree);
                }
                break;
            case RBF:
                // exp(-y|x-x'|^2)
                for (int i = 0; i < vectors.length; i++) {
                    kernel = 0.;
                    for (int j = 0; j < vectors[i].length; j++) {
                        kernel += Math.pow(vectors[i][j] - features[j], 2);
                    }
                    kernels[i] = Math.exp(-this.gamma * kernel);
                }
                break;
            case SIGMOID:
                // tanh(y<x,x'>+r)
                for (int i = 0; i < vectors.length; i++) {
                    kernel = 0.;
                    for (int j = 0; j < vectors[i].length; j++) {
                        kernel += vectors[i][j] * features[j];
                    }
                    kernels[i] = Math.tanh((this.gamma * kernel) + this.coef0);
                }
                break;
        }

        int[] starts = new int[this.nRows];
        for (int i = 0; i < this.nRows; i++) {
            if (i != 0) {
                int start = 0;
                for (int j = 0; j < i; j++) {
                    start += weights[j];
                }
                starts[i] = start;
            } else {
                starts[0] = 0;
            }
        }

        int[] ends = new int[this.nRows];
        for (int i = 0; i < this.nRows; i++) {
            ends[i] = weights[i] + starts[i];
        }

        if (this.nClasses == 2) {

            for (int i = 0; i < kernels.length; i++) {
                kernels[i] = -kernels[i];
            }

            double decision = 0.;
            for (int k = starts[1]; k < ends[1]; k++) {
                decision += kernels[k] * coefficients[0][k];
            }
            for (int k = starts[0]; k < ends[0]; k++) {
                decision += kernels[k] * coefficients[0][k];
            }
            decision += intercepts[0];

            if (decision > 0) {
                return 0;
            }
            return 1;

        }

        double[] decisions = new double[intercepts.length];
        for (int i = 0, d = 0, l = this.nRows; i < l; i++) {
            for (int j = i + 1; j < l; j++) {
                double tmp = 0.;
                for (int k = starts[j]; k < ends[j]; k++) {
                    tmp += coefficients[i][k] * kernels[k];
                }
                for (int k = starts[i]; k < ends[i]; k++) {
                    tmp += coefficients[j - 1][k] * kernels[k];
                }
                decisions[d] = tmp + intercepts[d];
                d++;
            }
        }

        int[] votes = new int[intercepts.length];
        for (int i = 0, d = 0, l = this.nRows; i < l; i++) {
            for (int j = i + 1; j < l; j++) {
                votes[d] = decisions[d] > 0 ? i : j;
                d++;
            }
        }

        int[] amounts = new int[this.nClasses];
        for (int i = 0, l = votes.length; i < l; i++) {
            amounts[votes[i]] += 1;
        }

        int classVal = -1, classIdx = -1;
        for (int i = 0, l = amounts.length; i < l; i++) {
            if (amounts[i] > classVal) {
                classVal = amounts[i];
                classIdx = i;
            }
        }
        return this.classes[classIdx];

    }

    public static char predictLetter(double[] features, Context c) {
        int retorno = -100;
        if (features.length == 18) {

            if (vectors == null) {
                vectors = lerVectors(c);
            }
            if (coefficients == null) {
                coefficients = lerCoefs(c);
            }
            if (intercepts == null) {
                intercepts=lerIntercepts(c);
            }
            if (weights == null) {
                weights=lerWeights(c);
            }

            // Prediction:
            SVC clf = SVC.getInstance(28, 28, vectors, coefficients, intercepts, weights, "rbf", 0.1, 0.0, 1);
            retorno = clf.predict(features);

        }
        return formatPredict(retorno);
    }

    private static int[] lerWeights(Context c) {
        int[] retorno = null;
        try {
            final InputStream inputStream = c.getResources().openRawResource(R.raw.weights);
            final InputStreamReader inputreader = new InputStreamReader(inputStream);
            BufferedReader buffreader = new BufferedReader(inputreader);
            final List<Integer> lista = new ArrayList<>();
            String line;
            while ((line = buffreader.readLine()) != null) {
                if (!line.isEmpty())
                    lista.add(Integer.parseInt(line));
            }
            retorno = new int[lista.size()];
            for (int i = 0; i < lista.size(); i++) {
                retorno[i] = lista.get(i);
            }
            buffreader.close();
            inputreader.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d("ceofsEX", e.getMessage());
        }
        Log.d("wheushasdw", Arrays.toString(retorno));
        return retorno;
    }


    private static double[] lerIntercepts(Context c) {
        double[] retorno = null;
        try {
            final InputStream inputStream = c.getResources().openRawResource(R.raw.intercepts);
            final InputStreamReader inputreader = new InputStreamReader(inputStream);
            BufferedReader buffreader = new BufferedReader(inputreader);
            final List<Double> lista = new ArrayList<>();
            String line;
            while ((line = buffreader.readLine()) != null) {
                if (!line.isEmpty())
                    lista.add(Double.parseDouble(line));
            }
            retorno = new double[lista.size()];
            for (int i = 0; i < lista.size(); i++) {
                retorno[i] = lista.get(i);
            }
            buffreader.close();
            inputreader.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d("ceofsEX", e.getMessage());
        }
        return retorno;
    }

    private static double[][] lerCoefs(Context c) {
        double[][] retorno = null;
        try {
            final InputStream inputStream = c.getResources().openRawResource(R.raw.coefs);
            final InputStreamReader inputreader = new InputStreamReader(inputStream);
            BufferedReader buffreader = new BufferedReader(inputreader);
            final List<List<Double>> listona = new ArrayList<>();
            String line;
            int maiorTamanho = 0;


            while ((line = buffreader.readLine()) != null) {


                final List<Double> listinha = new ArrayList<>();
                String[] linhaQuebrada = line.split(",");
                if (linhaQuebrada.length > 0) {
                    maiorTamanho = linhaQuebrada.length;
                }
                for (int i = 0; i < linhaQuebrada.length; i++) {
                    listinha.add(Double.parseDouble(linhaQuebrada[i]));
                }
                listona.add(listinha);
            }

            retorno = new double[listona.size()][maiorTamanho];
            for (int i = 0; i < listona.size(); i++) {
                for (int j = 0; j < maiorTamanho; j++) {
                    retorno[i][j] = listona.get(i).get(j);
                }
            }
            buffreader.close();
            inputreader.close();
            inputStream.close();

        } catch (IOException e) {
            Log.d("ceofsEX", e.getMessage());
        }


        return retorno;
    }

    private static double[][] lerVectors(Context c) {
        double[][] retorno = null;
        try {
            final InputStream inputStream = c.getResources().openRawResource(R.raw.vectors);
            final InputStreamReader inputreader = new InputStreamReader(inputStream);
            BufferedReader buffreader = new BufferedReader(inputreader);
            final List<List<Double>> listona = new ArrayList<>();
            String line;
            int maiorTamanho = 0;
            while ((line = buffreader.readLine()) != null) {

                final List<Double> listinha = new ArrayList<>();
                String[] linhaQuebrada = line.split(",");
                if (linhaQuebrada.length > 0) {
                    maiorTamanho = linhaQuebrada.length;
                }
                for (int i = 0; i < linhaQuebrada.length; i++) {
                    listinha.add(Double.parseDouble(linhaQuebrada[i]));
                }
                listona.add(listinha);
            }

            retorno = new double[listona.size()][maiorTamanho];
            for (int i = 0; i < listona.size(); i++) {
                for (int j = 0; j < maiorTamanho; j++) {
                    retorno[i][j] = listona.get(i).get(j);
                }
            }
            buffreader.close();
            inputreader.close();
            inputStream.close();

        } catch (IOException e) {
            Log.d("vectorEX", e.getMessage());
        }


        return retorno;
    }

    private static char formatPredict(int pred) {
        if (pred < 26)
            return (char) (pred + 65);
        else if (pred == 26) {
            return ' ';
        } else {
            return (char)0;
        }
    }
}