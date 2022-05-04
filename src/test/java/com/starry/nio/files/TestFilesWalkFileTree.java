package com.starry.nio.files;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author starry
 * @version 1.0
 * @date 2022/1/28 21:45
 * @Description
 */
public class TestFilesWalkFileTree {

    public static void main(String[] args) throws IOException {
        String source = "C:\\Users\\starry\\Desktop\\test";
        String target = "C:\\Users\\starry\\Desktop\\test_target";
        Files.walk(Paths.get(source)).forEach(path -> {
            try {
                // 原路径进行替换
                String targetPath = path.toString().replace(source, target);
                if (Files.isDirectory(path)) {
                    // 文件夹
                    Files.createDirectories(Paths.get(targetPath));
                } else if (Files.isRegularFile(path)) {
                    // 常规文件
                    Files.createFile(Paths.get(targetPath));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        System.out.println("复制完成");
    }

    private static void m3() throws IOException {
        Files.walkFileTree(Paths.get("C:\\Users\\starry\\Desktop\\test"), new SimpleFileVisitor<Path>() {
            /**
             * 查看文件
             *
             * @param file
             * @param attrs
             * @return
             * @throws IOException
             */
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return super.visitFile(file, attrs);
            }

            /**
             * 查看文件夹之后
             *
             * @param dir
             * @param exc
             * @return
             * @throws IOException
             */
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
        System.out.println("删除完成");
    }

    private static void m2() throws IOException {
        AtomicInteger jarCount = new AtomicInteger();

        Files.walkFileTree(Paths.get("D:\\environment\\Java\\jdk1.8.0_291"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".jar")) {
                    jarCount.incrementAndGet();
                }
                return super.visitFile(file, attrs);
            }
        });
        System.out.println("jarCount = " + jarCount);
    }

    private static void m1() throws IOException {
        AtomicInteger dirCount = new AtomicInteger();
        AtomicInteger fileCount = new AtomicInteger();
        Files.walkFileTree(Paths.get("D:\\environment\\Java\\jdk1.8.0_291"), new SimpleFileVisitor<Path>() {
            /**
             * 查看目录前
             * @param dir
             * @param attrs
             * @return
             * @throws IOException
             */
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("===>" + dir);
                dirCount.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }

            /**
             * 查看文件
             * @param file
             * @param attrs
             * @return
             * @throws IOException
             */
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file);
                fileCount.incrementAndGet();
                return super.visitFile(file, attrs);
            }
        });
        System.out.println("dirCount = " + dirCount);
        System.out.println("fileCount = " + fileCount);
    }
}
