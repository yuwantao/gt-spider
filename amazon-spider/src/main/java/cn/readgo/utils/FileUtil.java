package cn.readgo.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class FileUtil {
    private static final Log logger = LogFactory.getLog(FileUtil.class);
    
    public static void delFileIfExist(String pathname) {
    	File file = new File(pathname);
    	if(file.exists() && file.isFile()) {
    		file.delete();
    	}
    }

    public static void newFolder(String folderPath) {
        try {
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            if (!myFilePath.exists()) {
            	myFilePath.mkdirs();
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public static void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);
                delFolder(path + "/" + tempList[i]);
            }
        }
    }

    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath);
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete();

        } catch (Exception e) {
            logger.error(e);
        }
    }

    public static byte[] getBytesFromFile(File f) {
        if (f == null) {
            return null;
        }
        try {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);

            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1) {
                out.write(b, 0, n);
            }
            stream.close();
            out.close();

            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static File getFileFromBytes(byte[] b, String outputFile) {
        BufferedOutputStream stream = null;
        File file = null;
        try {
            file = new File(outputFile);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }

    public static Object getObjectFromBytes(byte[] objBytes) throws Exception {
        if (objBytes == null || objBytes.length == 0) {
            return null;
        }

        ByteArrayInputStream bi = new ByteArrayInputStream(objBytes);
        ObjectInputStream oi = new ObjectInputStream(bi);

        return oi.readObject();
    }

    public static byte[] getBytesFromObject(Serializable obj) throws Exception {
        if (obj == null) {
            return null;
        }

        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(obj);

        return bo.toByteArray();
    }

    /**
     *
     * @param pathname 要写入的文件路径
     * @param list 要写入的内容
     * @param append 是否追加
     * @throws IOException
     */
    public static void writeContent(String pathname, List<String> list, boolean append) {
        if(list == null || list.size() == 0) {
            return;
        }
        File file = new File(pathname);
        String parentDir = file.getParent();
        newFolder(parentDir);
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file, append));
            for (String string : list) {
                bufferedWriter.write(string + "\n");
            }
            bufferedWriter.flush();
        } catch (IOException e) {
            logger.error("", e);
        } finally {
            if(bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
        }
    }

    /**
     *
     * @param pathname 文件路径
     * @param content 要写入的内容
     * @param append 是否追加
     * @throws IOException
     */
    public static void writeContent(String pathname, String content, boolean append) {
        File file = new File(pathname);
        String parentDir = file.getParent();
        newFolder(parentDir);
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file, append));
            bufferedWriter.write(content + "\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            logger.error("", e);
        } finally {
            if(bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
        }
    }
    
    
    public static void readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            long ts = System.currentTimeMillis();
            logger.info("start to file info...");
            while ((tempString = reader.readLine()) != null) {
               System.out.println("line " + line + ": " + tempString);
                line++;
            }
            long te = System.currentTimeMillis();
			logger.info("file info done. used time(minute):" + (te -ts)/1000.0/60.0);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    public static void main(String[] hlq){

    }
    
	public static List<String> readFileByLinesToList(String fileName) {
    	List<String> list=new ArrayList<String>();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
			int line = 1;
            long ts = System.currentTimeMillis();
            logger.info("start to file info...");
            while ((tempString = reader.readLine()) != null) {
//               System.out.println("line " + line + ": " + tempString);
            	list.add(tempString);
                line++;
            }
            long te = System.currentTimeMillis();
			logger.info("file info done. used time(minute):" + (te -ts)/1000.0/60.0);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return list;
    }

	
}