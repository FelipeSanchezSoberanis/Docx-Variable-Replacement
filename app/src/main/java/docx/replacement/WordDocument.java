package docx.replacement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WordDocument {
  private File document;

  public void unzipTo(File file) throws IOException {
    try (ZipFile zipFile = new ZipFile(getDocument())) {
      zipFile.extractAll(file.getPath());
    }
  }

  public void zipTo(File contentFolder, File zipDestination) throws IOException {
    File[] files = contentFolder.listFiles();

    try (ZipFile zipFile = new ZipFile(zipDestination)) {
      for (File file : files) {
        if (file.isDirectory()) zipFile.addFolder(file);
        else zipFile.addFile(file);
      }
    }
  }

  public void replaceVariables(
      Map<String, String> replacementPerVariable, File newDocument, File tempDir)
      throws IOException {
    unzipTo(tempDir);

    File xmlFile = Paths.get(tempDir.getPath(), "word", "document.xml").toFile();

    XmlReplacer xmlReplacer = new XmlReplacer(xmlFile);
    String newXmlData = xmlReplacer.replace(replacementPerVariable);

    try (FileOutputStream fos = new FileOutputStream(xmlFile)) {
      fos.write(newXmlData.getBytes(StandardCharsets.UTF_8));
    }

    try {
      zipTo(tempDir, newDocument);
    } catch (ZipException e) {

    }

    deleteDirectory(tempDir);
  }

  private void deleteDirectory(File file) {
    File[] contents = file.listFiles();
    if (contents != null) {
      for (File f : contents) {
        if (!Files.isSymbolicLink(f.toPath())) {
          deleteDirectory(f);
        }
      }
    }
    file.delete();
  }
}
