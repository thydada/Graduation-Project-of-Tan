from barcode import Code128
from barcode.writer import ImageWriter


def generate_barcode(data: str, output_filename: str = "barcode"):
    """
    生成更易扫码的一维码图片：
    - 放大条形码尺寸
    - 增加四周空白（安静区）
    - 调整字体和文本距离
    """
    writer = ImageWriter()
    # 关键参数说明：
    # - module_width: 单条线的宽度（越大，整体越宽）
    # - module_height: 条码高度
    # - quiet_zone: 条码四周的空白区域（毫米）
    # - font_size: 下面文字大小
    # - text_distance: 条码与文字之间的距离
    options = {
        "module_width": 0.4,   # 条纹宽度
        "module_height": 10.0, # 条纹高度，值越小条码越矮
        "quiet_zone": 8.0,     # 默认 6，一般不要小于 6
        "write_text": False,   # 不在图片下方显示文字
        "dpi": 300,            # 提高分辨率，便于打印、屏幕显示
    }
    Code128(data, writer=writer).save(output_filename, options)


if __name__ == "__main__":
    content = input("请输入条形码内容：").strip()
    if not content:
        raise ValueError("条形码内容不能为空")
    name = input("请输入输出文件名（默认：barcode）：").strip() or "barcode"
    generate_barcode(content, name)
    print("done")

