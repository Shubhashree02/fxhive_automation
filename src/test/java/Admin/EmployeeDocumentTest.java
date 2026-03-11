package Admin;

import AdminPage.EmployeeDocumentPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.stream.Stream;

/**
 * Test: Employee Document → Select employee → Upload Document → select document type, document name, file → Upload.
 */
public class EmployeeDocumentTest {
    private WebDriver driver;

    @BeforeClass(alwaysRun = true)
    public void init() {
        driver = StageSuiteSession.getDriver();
    }

    @Test
    public void testUploadEmployeeDocument() throws Exception {
        StageSuiteSession.ensureOnDashboard();
        EmployeeDocumentPage docPage = new EmployeeDocumentPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Step 1: Click Employee Document menu
        docPage.clickEmployeeDocumentMenu();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("employee-document-select")));

        // Step 2: Select a random employee from the dropdown
        docPage.selectRandomEmployee();

        // Step 3: Click Upload Document (opens modal) - wait for button to become enabled
        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        docPage.clickUploadDocumentButton();

        // Step 4: In upload modal - select document type (if combobox present)
        try {
            docPage.selectDocumentType("PAN Card");
        } catch (Exception e) {
            // Document type might have different options; try first option or skip
        }

        // Step 5: Enter document name
        docPage.enterDocumentName("PAN Card");

        // Step 6: Upload PDF file from user's Downloads (file field accepts only PDF)
        Path pdfFromDownloads = findDownloadFileByExtension("pdf");
        docPage.uploadFile(pdfFromDownloads.toAbsolutePath().toString());

        // Step 7: Click Upload (submit in modal)
        docPage.clickUploadSubmit();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("✅ Employee document upload completed (PDF only).");
    }

    /** Builds a minimal valid PDF (single empty page) for upload (fallback, currently unused). */
    private static byte[] buildMinimalPdf() {
        String header = "%PDF-1.4\n";
        String s1 = "1 0 obj\n<</Type/Catalog/Pages 2 0 R>>\nendobj\n";
        String s2 = "2 0 obj\n<</Type/Pages/Kids[3 0 R]/Count 1>>\nendobj\n";
        String s3 = "3 0 obj\n<</Type/Page/Parent 2 0 R/MediaBox[0 0 612 792]>>\nendobj\n";
        int pos1 = header.getBytes(StandardCharsets.ISO_8859_1).length;
        int pos2 = pos1 + s1.getBytes(StandardCharsets.ISO_8859_1).length;
        int pos3 = pos2 + s2.getBytes(StandardCharsets.ISO_8859_1).length;
        String xref = "xref\n0 4\n" +
            "0000000000 65535 f \n" +
            String.format("%010d 00000 n \n", pos1) +
            String.format("%010d 00000 n \n", pos2) +
            String.format("%010d 00000 n \n", pos3) +
            "trailer\n<</Size 4/Root 1 0 R>>\nstartxref\n";
        String full = header + s1 + s2 + s3 + xref;
        int startxrefPos = full.getBytes(StandardCharsets.ISO_8859_1).length;
        full += startxrefPos + "\n%%EOF\n";
        return full.getBytes(StandardCharsets.ISO_8859_1);
    }

    /**
     * Finds the first file in the user's Downloads folder matching one of the given extensions.
     * Throws an exception if none is found so the test clearly reports the missing prerequisite.
     */
    private static Path findDownloadFileByExtension(String... exts) throws Exception {
        Path downloadsDir = Path.of(System.getProperty("user.home"), "Downloads");
        if (!Files.isDirectory(downloadsDir)) {
            throw new IllegalStateException("Downloads folder not found at: " + downloadsDir);
        }
        try (Stream<Path> stream = Files.list(downloadsDir)) {
            return stream
                .filter(Files::isRegularFile)
                .filter(p -> {
                    String name = p.getFileName().toString().toLowerCase();
                    for (String ext : exts) {
                        if (name.endsWith("." + ext.toLowerCase())) {
                            return true;
                        }
                    }
                    return false;
                })
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                    "No file with extensions " + String.join(", ", exts) + " found in Downloads: " + downloadsDir));
        }
    }
}
