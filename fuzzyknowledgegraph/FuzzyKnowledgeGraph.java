package fuzzyknowledgegraph;

import java.util.*;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class FuzzyKnowledgeGraph {
	
    // Mô phỏng dữ liệu mẫu: Tên thuộc tính - {Giá trị}
    private HashMap<String, String[]> properties;

    public FuzzyKnowledgeGraph() {
        properties = new HashMap<>();
        // Mô phỏng dữ liệu mẫu
       
        properties.put("Tuổi", new String[]{"Low", "Medium", "High", "Very high", "Extremely high"});
        properties.put("Tuổi thai", new String[] {"Low", "Medium"});
        properties.put("Công việc", new String[]{"Medium", "High"});
        properties.put("Chỉ số BMI", new String[]{"Low", "Medium", "High", "Very high", "Extremely high"});
        properties.put("Số lần có thai", new String[]{"Low", "Medium", "High"});
        properties.put("Huyết áp trên (mmHg)", new String[]{"Low", "Medium", "High", "Very high", "Extremely high"});
        properties.put("Huyết áp dưới (mmHg)", new String[]{"Low", "Medium", "High", "Very high", "Extremely high"});
        properties.put("Chỉ số HGB (g/l)", new String[]{"Very high", "High", "Medium", "Low", "Very low", "Extremely low"});
        properties.put("Chỉ số PLT (g/l)", new String[]{"Low", "Medium", "High"});
        properties.put("Chỉ số URE máu (mmol/l)", new String[]{"Low", "Medium", "High"});
        properties.put("Chỉ số CREATININ (umol/l)", new String[]{"Low", "Medium", "High", "Very high", "Extremely high"});
        properties.put("Chỉ số ACID URIC (umol/l)", new String[]{"Medium", "High", "Very high", "Extremely high"});
        properties.put("Chỉ số ALT (UI/l)", new String[]{"Medium", "High", "Very high", "Extremely high"});
        properties.put("Chỉ số AST (UI/l)", new String[]{"Medium", "High", "Very high", "Extremely high"});
        properties.put("Chỉ số Protein toàn phần (g/l)", new String[]{"Low", "Medium", "High"});
        properties.put("Chỉ số ALBUMIN (g/l)", new String[]{"Low", "Medium", "High"});
        properties.put("Chỉ số LDH (U/I)", new String[]{"Low", "Medium", "High"});
        properties.put("Chỉ số Protein niệu (g/l)", new String[]{"Medium", "High"});
        // Thêm các thuộc tính khác vào đây
    }
    public static int combination(int k, int n) {
        if (k == 0 || k == n) {
            return 1;
        }
        if (k == 1) {
            return n;
        }
        return combination(k - 1, n - 1) + combination(k, n - 1);
    }
    public static double[][] calculateA(List<List<String>> base) {
        int column = base.get(0).size();
        int row = base.size();
        int combinations = combination(4, column - 1);
        double[][] A = new double[row][combinations];

        for (int r1 = 0; r1 < row; r1++) {
            int[] k = new int[combinations];
            int temp = 0;
            for (int a = 0; a < column - 4; a++) {
                for (int b = a + 1; b < column - 3; b++) {
                    for (int c = b + 1; c < column - 2; c++) {
                        for (int d = c + 1; d < column - 1; d++) {
                            for (int r2 = 0; r2 < row; r2++) {
                                if (base.get(r1).get(a).equals(base.get(r2).get(a)) && base.get(r1).get(b).equals(base.get(r2).get(b))
                                        && base.get(r1).get(c).equals(base.get(r2).get(c)) && base.get(r1).get(d).equals(base.get(r2).get(d))) {
                                    k[temp]++;
                                }
                            }
                            A[r1][temp] = (double) k[temp] / row;
                            temp++;
                        }
                    }
                }
            }
        }
        return A;
    }
    public static double[][] calculateM(List<List<String>> base) {
        int column = base.get(0).size();
        int row = base.size();
        double[][] M = new double[row][column - 1];

        for (int t1 = 0; t1 < row; t1++) {
            int[] k = new int[row];
            int temp = 0;
            for (int i = 0; i < column - 1; i++) {
                for (int t2 = 0; t2 < row; t2++) {
                    if (base.get(t1).get(i).equals(base.get(t2).get(i)) && base.get(t1).get(column -1 ).equals(base.get(t2).get(column - 1))) {
                        k[temp]++;
                    }
                }
                M[t1][temp] = (double) k[temp] / row;
                temp++;
            }
        }
        return M;
    }
    public static double[][] calculateB(List<List<String>> base, double[][] A, double[][] M) {
        int column = base.get(0).size();
        int row = base.size();
        double[][] B = new double[row][combination(4, column - 1)];

        for (int r = 0; r < row; r++) {
            int temp = 0;
            for(int a1 = 0; a1 < column - 4; a1++) {
            	for (int a = a1+1; a < column - 3; a++) {
                    for (int b = a + 1; b < column - 2; b++) {
                        for (int c = b + 1; c < column - 1; c++) {
                            double minM = Math.min(Math.min(Math.min(M[r][a], M[r][b]), M[r][c]), M[r][a1]);
                            B[r][temp] = sumOfArray(A[r]) * minM;
                            temp++;
                        }
                    }
                }
            }
        }
        return B;
    }
    public static double sumOfArray(double[] arr) {
        double sum = 0;
        for (double num : arr) {
            sum += num;
        }
        return sum;
    }    
    public static double[][] calculateC(List<List<String>> base, double[][] B) {
        int column = base.get(0).size();
        int row = base.size();
        int cols = 3 * combination(4, column - 1);// nhân 3 vì có 3 nhãn đầu ra
        double[][] C = new double[row][cols];

        for (int r1 = 0; r1 < row; r1++) {
            int temp = 0;
            for (int i = 0; i < 3; i++) {
                for(int a1 = 0; a1 < column - 4; a1++) {
                	for (int a = a1 + 1; a < (column - 3); a++) {
                        for (int b = a + 1; b < (column - 2); b++) {
                            for (int c = b + 1; c < (column - 1); c++) {
                                for (int r2 = 0; r2 < row; r2++) {
                                    if(base.get(r1).get(a1).equals(base.get(r2).get(a1)) && base.get(r1).get(a).equals(base.get(r2).get(a)) && base.get(r1).get(b).equals(base.get(r2).get(b)) && base.get(r1).get(c).equals(base.get(r2).get(c)) && base.get(r1).get(column - 1).equals(base.get(r2).get(column - 1)) && base.get(r1).get(column - 1).equals(String.valueOf(i))) {
                                    	C[r1][temp] += B[r2][temp % combination(4, column - 1)];
                                    }
                                }
                                temp++;
                            }
                        }
                    }
                }
            }
        }
        return C;
    }

    // Hàm tính độ đồng thuận FISA
    public static int fisa(List<List<String>> base, double[][] C, List<String> list) {
        int column = base.get(0).size();
        int row = base.size();

        int cols = combination(4, (column - 1));
        double[] C0 = new double[cols];
        double[] C1 = new double[cols];
        double[] C2 = new double[cols];

        int t = 0;
        for(int a1 = 0; a1 < column - 4; a1++) {
        	for (int a = a1 + 1; a < column - 3; a++) {
                for (int b = a + 1; b < column - 2; b++) {
                    for (int c = b + 1; c < column - 1; c++) {
                        for (int r = 0; r < row - 1; r++) {
                            if (base.get(r).get(a1).equals(list.get(a1)) && base.get(r).get(a).equals(list.get(a)) && base.get(r).get(b).equals(list.get(b)) && base.get(r).get(c).equals(list.get(c)) && Integer.parseInt(base.get(r).get(column - 1)) == 0) {
                                C0[t] = C[r][t + 0 * cols];
                                break;
                            }
                            if (base.get(r).get(a1).equals(list.get(a1)) && base.get(r).get(a).equals(list.get(a)) && base.get(r).get(b).equals(list.get(b)) && base.get(r).get(c).equals(list.get(c)) && Integer.parseInt(base.get(r).get(column - 1)) == 1) {
                                C1[t] = C[r][t + 1 * cols];
                                break;
                            }
                            if (base.get(r).get(a1).equals(list.get(a1)) && base.get(r).get(a).equals(list.get(a)) && base.get(r).get(b).equals(list.get(b)) && base.get(r).get(c).equals(list.get(c)) && Integer.parseInt(base.get(r).get(column - 1)) == 2) {
                                C2[t] = C[r][t + 2 * cols];
                                break;
                            }
                        }
                        t++;
                    }
                }
            }
        }

        double D0 = getMax(C0) + getMin(C0);
        double D1 = getMax(C1) + getMin(C1);
        double D2 = getMax(C2) + getMin(C2);

        if (D0 > D1 && D0 > D2) {
            return 0;
        } else if (D1 > D2) {
            return 1;
        } else {
            return 2;
        }
    }
    private static double getMax(double[] arr) {
        double max = arr[0];
        for (double num : arr) {
            if (num > max) {
                max = num;
            }
        }
        return max;
    }

    private static double getMin(double[] arr) {
        double min = arr[0];
        for (double num : arr) {
            if (num < min) {
                min = num;
            }
        }
        return min;
    }
    public static void testAccuracy(List<List<String>> base, double[][] C) {
    	String fileName = "DataTest.txt"; // Thay đổi tên file tương ứng
    	List<List<String>> test = importFuzzyData(fileName);
        
        int X[] = new int[test.size()];
        for (int i = 0; i < test.size(); i++) {
            X[i] = fisa(base, C, test.get(i));
        }
        writeToTextFile("Result", X, "Result.txt");
        
    }
    // Dự đoán nhãn cho mẫu dữ liệu mới
    
    public static List<List<String>> importFuzzyData(String fileName) {
    	List<List<String>> base = new ArrayList<>();
        BufferedReader reader = null;

        try {
            // Mở file văn bản
            reader = new BufferedReader(new FileReader(fileName));
            String line;

            // Đọc từng dòng trong file
            int k = 0;
            while ((line = reader.readLine()) != null) {
                // Tách các giá trị trên dòng và thêm vào base
            	String[] tokens = line.split("\t"); // Phân chia dữ liệu bằng dấu tab

                List<String> row = new ArrayList<>();
                for (String token : tokens) {
                    row.add(token);
                }
                base.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Đóng luồng đọc file
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return base;
    }
    public static void update() {
        String fileName = "Data.txt"; // Thay đổi tên file tương ứng
        List<List<String>> base = importFuzzyData(fileName);
        double[][] A = calculateA(base);
        double[][] M = calculateM(base);
        double[][] B = calculateB(base, A, M);
        double[][] C = calculateC(base, B);
        writeToTextFile("A", A, "MTA.txt");
        writeToTextFile("M", M, "MTM.txt");
        writeToTextFile("B", B, "MTB.txt");
        writeToTextFile("C", C, "MTC.txt");
        testAccuracy(base, C);
        System.out.println("Update successful! Let's Start");
    }
    
    public static void writeToTextFile(String sheetName, int[] data, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
        	writer.write(sheetName + ":\n");
            for (int value : data) {
                writer.write(value + "\n");
            }
            writer.write("\n\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeToTextFile(String sheetName, double[][] data, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(sheetName + ":\n");
            for (double[] row : data) {
                StringBuilder line = new StringBuilder();
                for (double cell : row) {
                    line.append(cell).append(" ");
                }
                writer.write(line.toString().trim() + "\n");
            }
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
    	String fileName = "Data.txt"; // Thay đổi đường dẫn file tương ứng
    	long startTime = System.currentTimeMillis(); // Bắt đầu đếm thời gian

        // Gọi phương thức update()
        update();
        
        long endTime = System.currentTimeMillis(); // Kết thúc đếm thời gian
        long executionTime = endTime - startTime; // Tính thời gian thực thi
        System.out.println("Time: " + executionTime + " milliseconds");
        // Sử dụng biến base ở đây
        // Tạo một đối tượng FuzzyKnowledgeGraph
        FuzzyKnowledgeGraph knowledgeGraph = new FuzzyKnowledgeGraph();

        // Dữ liệu mẫu mới cần dự đoán
        String[][] newData = {
            {"Tuổi", "Nhỏ"},
            {"Công việc", "Cán bộ"}
            // Thêm các thuộc tính và giá trị tương ứng ở đây
        };

        // Dự đoán nhãn cho dữ liệu mới
        
    }
}

