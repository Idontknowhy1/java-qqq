type ProgressCallback = (loaded: number, total: number | null, done: boolean) => void;

/**
 * 下载文件并显示进度
 * @param url - 要下载的文件URL
 * @param onProgress - 进度回调函数，接收已加载字节数、总字节数和完成标志
 * @returns Promise<void>
 */
const downloadFile = ( url: string, filePath: string, onProgress?: ProgressCallback
): Promise<void> => {
    return fetch(url)
        .then((response: Response) => {
            if (!response.ok) {
                if (onProgress) {
                    onProgress(1, 1, false)
                }
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const contentLength: string | null = response.headers.get('content-length');
            const total: number | null = contentLength ? parseInt(contentLength, 10) : null;
            let loaded: number = 0;
            const chunks: Uint8Array[] = []; // 存储接收到的数据块

            // 检查是否能够计算进度
            if (!contentLength) {
                console.warn('Content-Length header is missing, progress tracking will be limited');
                if (onProgress) {
                    onProgress(1, 1, false)
                }
            }

            // 获取可读流的读取器
            const reader: ReadableStreamDefaultReader<Uint8Array> = response.body.getReader();

            // 递归读取数据流
            function read(): Promise<Uint8Array[]> {
                return reader.read().then(({ done, value }: { done: boolean; value?: Uint8Array }) => {
                    if (done) {
                        return chunks;
                    }

                    // 确保value存在（当done为false时，value应该存在）
                    if (!value) {
                        throw new Error('Unexpected: value is undefined when done is false');
                    }

                    // 更新已加载的字节数
                    loaded += value.length;
                    chunks.push(value);

                    // 调用进度回调
                    if (onProgress) {
                        onProgress(loaded, total, false);
                    }

                    // 继续读取下一块数据
                    return read();
                });
            }

            return read().then((chunks: Uint8Array[]) => {
                // 将所有数据块合并成一个 Uint8Array
                const chunksAll: Uint8Array = new Uint8Array(loaded);
                let position: number = 0;
                for (let chunk of chunks) {
                    chunksAll.set(chunk, position);
                    position += chunk.length;
                }

                // 创建 Blob 对象
                return new Blob([chunksAll]);
            });
        })
        .then((blob: Blob) => {
            // 获取到文件数据后，进行保存
            saveFileToDisk(blob, filePath, onProgress);
        })
        .catch((error: Error) => {
            console.error('下载失败:', error)
            if (onProgress) {
                onProgress(1, 1, false)
            }
        });
}

/**
 * 将Blob对象保存到本地文件
 * @param blob - 文件数据的Blob对象
 * @param filename - 要保存的文件名
 */
function saveFileToDisk(blob: Blob, filename: string, onProgress?: ProgressCallback): void {
    // 将Blob转换为ArrayBuffer或BinaryString，以便写入
    let reader: FileReader = new FileReader();

    reader.onload = function (): void {
        // 检查reader.result是否为ArrayBuffer
        if (!(reader.result instanceof ArrayBuffer)) {
            throw new Error('Expected ArrayBuffer from FileReader');
        }

        // 将 ArrayBuffer 转换为 Base64 字符串
        const uint8Array: Uint8Array = new Uint8Array(reader.result);
        let binaryString: string = '';
        for (let i = 0; i < uint8Array.length; i++) {
            binaryString += String.fromCharCode(uint8Array[i]);
        }
        const base64String: string = window.btoa(binaryString);

        // 使用CEP的fs.writeFile方法写入文件
        const result = window.cep.fs.writeFile(filename, base64String, window.cep.encoding.Base64);
        if (result.err === 0) {
            if (onProgress) {
                onProgress(1, 1, true)
            }
        } else {
            if (onProgress) {
                onProgress(1, 1, false)
            }
        }
    };

    reader.onerror = function (): void {
        console.error('FileReader error occurred while reading the blob');
        if (onProgress) {
            onProgress(1, 1, false)
        }
    };

    reader.readAsArrayBuffer(blob);
}

export default downloadFile