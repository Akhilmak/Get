import yt_dlp

def list_formats(url):
    ydl_opts = {
        'format': 'best',  # Get the best format by default
        'quiet': True,     # Suppress output except for errors
    }

    with yt_dlp.YoutubeDL(ydl_opts) as ydl:
        info_dict = ydl.extract_info(url, download=False)  # Extract info without downloading
        formats = info_dict.get('formats', [])
    return [(f['format_id'], f.get('ext'), f.get('height'), f['url']) for f in formats]

def download_video(url, format_code):
    ydl_opts = {
        'format': format_code,
        'outtmpl': '/storage/emulated/0/Download/%(title)s.%(ext)s',  # Save to Download folder
    }

    with yt_dlp.YoutubeDL(ydl_opts) as ydl:
        ydl.download([url])