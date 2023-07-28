package docx.replacement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class App {
  public static void main(String[] args) throws IOException {
    File docxFile = Paths.get("src", "main", "resources", "testResources", "input.docx").toFile();
    File newDocxFile =
        Paths.get("src", "main", "resources", "testResources", "output.docx").toFile();
    File tempDir = Paths.get("src", "main", "resources", "testResources", "temp").toFile();
    Map<String, String> replacementPerVariable = new HashMap<>();
    replacementPerVariable.put("nombre", "Felipe");
    replacementPerVariable.put("apellidos", "Sánchez Soberanis");
    replacementPerVariable.put(
        "direccion", "Calle 14 #365 x 15 y 15a, Paraíso Maya, Mérida, Yucatán, México");
    replacementPerVariable.put("inquilino", "José Eduardo Escamilla Sansores");

    WordDocument wordDocument = new WordDocument(docxFile);
    wordDocument.replaceVariables(replacementPerVariable, newDocxFile, tempDir);
  }
}
