import yt_dlp

def list_formats(url):
    ydl_opts = {
        'quiet': True,
        'format': 'bestvideo+bestaudio/best',
    }

    with yt_dlp.YoutubeDL(ydl_opts) as ydl:
        info_dict = ydl.extract_info(url, download=False)
        # print("Info Dictionary: ", info_dict)
        formats = info_dict.get('formats', [])

    # print("Available Formats: ", formats)

    format_list = []
    for f in formats:
        # Create a dictionary for each format
        format_info = {
            'format_id': f.get('format_id', 'N/A'),
            'ext': f.get('ext', 'N/A'),
            'height': f.get('height', 'N/A'),
            'format_note': f.get('format_note', 'N/A'),
            'url': f.get('url', '')
        }


        # print(f"Format: {format_info}")

        if all(value not in [None, 'N/A', ''] for value in format_info.values()):
            format_list.append(format_info)

    return format_list

def download_selected_format(url, format_id, progress_callback):
    def progress_hook(d):
        if d['status'] == 'downloading':
            downloaded_bytes = d.get('downloaded_bytes', 0)
            total_bytes = d.get('total_bytes', 1)
            progress = (downloaded_bytes / total_bytes) * 100
            progress_callback(int(progress))

    ydl_opts = {
        'format': format_id,
        'progress_hooks': [progress_hook],
        'outtmpl': '/storage/emulated/0/Download/%(title)s.%(ext)s',
    }

    with yt_dlp.YoutubeDL(ydl_opts) as ydl:
        ydl.download([url])
