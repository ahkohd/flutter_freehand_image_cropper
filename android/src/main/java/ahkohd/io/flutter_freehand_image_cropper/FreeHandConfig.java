package ahkohd.io.flutter_freehand_image_cropper;

public class FreeHandConfig {
    public String filePath;
    public String targetPath;
    public boolean addBorder;
    public int borderWidth;
    public int borderColor;
    public int paintStrokeColor;
    public int paintStrokeWidth;
    public int outputQuality;

    FreeHandConfig(
            String filePath,
            String targetPath,
            boolean addBorder,
            int borderWidth,
            int borderColor,
            int paintStrokeColor,
            int paintStrokeWidth,
            int outputQuality
    ) {
        this.filePath = filePath;
        this.targetPath = targetPath;
        this.addBorder = addBorder;
        this.borderWidth = borderWidth;
        this.borderColor = borderColor;
        this.paintStrokeColor = paintStrokeColor;
        this.paintStrokeWidth = paintStrokeWidth;
        this.outputQuality = outputQuality;
    }
}
