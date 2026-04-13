/**
 * 确保文件夹存在（先检查存在性，不存在则递归创建）
 * @param folderPath - 要确保存在的文件夹路径
 * @returns 操作结果对象
 */
export function ensureFolder(folderPath: string): { success: boolean; error?: string } {
    // 1. 首先检查CEP API可用性
    if (!window.cep || !window.cep.fs) {
        return { success: false, error: 'CEP文件系统API不可用' };
    }

    // 2. 调用ensureFolderExists检查文件夹状态
    const existenceCheck = ensureFolderExists(folderPath);
    
    if (existenceCheck.success) {
        // 文件夹已存在或已成功创建
        return existenceCheck;
    }
    
    // 3. 如果ensureFolderExists失败，尝试解析错误原因
    // 判断是否为"路径不存在"错误（错误码2或其他相关提示）
    const isNotFoundError = existenceCheck.error?.includes('错误码: 2') || 
                           existenceCheck.error?.includes('不存在') ||
                           existenceCheck.error?.toLowerCase().includes('not found');
    
    if (isNotFoundError) {
        // 4. 路径不存在，尝试递归创建多级目录
        console.log(`路径不存在，尝试递归创建: ${folderPath}`);
        return createFolderRecursive(folderPath);
    } else {
        // 5. 其他错误（如权限问题、I/O错误等），直接返回原错误
        return existenceCheck;
    }
}

// 依赖函数（基于之前讨论的实现）
/**
 * 检查文件夹是否存在（包含基础创建能力）
 */
function ensureFolderExists(folderPath: string): { success: boolean; error?: string } {
    if (!window.cep?.fs) {
        return { success: false, error: 'CEP文件系统API不可用' };
    }

    try {
        const statResult: { err: number; data?: any } = window.cep.fs.stat(folderPath);
        if (statResult.err === 0) {
            if (statResult.data?.isDirectory?.()) {
                return { success: true };
            } else {
                return { success: false, error: `路径已存在但不是文件夹: ${folderPath}` };
            }
        } else { //if (statResult.err === 2) {
            // 尝试创建单级目录
            const createResult = window.cep.fs.makedir(folderPath);
            if (createResult.err === 0) {
                return { success: true };
            } else {
                return { success: false, error: `文件夹创建失败，错误码: ${createResult.err}` };
            }
        } 
        // else {
        //     return { success: false, error: `检查文件夹状态失败，错误码: ${statResult.err}` };
        // }
    } catch (error) {
        const errorMessage = error instanceof Error ? error.message : '未知错误';
        return { success: false, error: `操作异常: ${errorMessage}` };
    }
}

/**
 * 递归创建多级目录
 */
function createFolderRecursive(folderPath: string): { success: boolean; error?: string } {
    if (!window.cep?.fs) {
        return { success: false, error: 'CEP文件系统API不可用' };
    }

    try {
        const normalizedPath = folderPath.replace(/\\/g, '/');
        const segments = normalizedPath.split('/').filter(Boolean);
        
        if (segments.length === 0) {
            return { success: false, error: '路径无效' };
        }

        let currentPath = '';
        
        // 处理绝对路径
        if (normalizedPath.startsWith('/')) {
            currentPath = '/';
        } else if (normalizedPath.includes(':')) {
            // Windows路径处理
            currentPath = segments[0] + '/';
            segments.shift();
        }

        for (const segment of segments) {
            currentPath = currentPath ? `${currentPath}/${segment}` : segment;
            
            const statResult = window.cep.fs.stat(currentPath);
            
            if (statResult.err === 2) {
                const createResult = window.cep.fs.makedir(currentPath);
                if (createResult.err !== 0) {
                    return { success: false, error: `创建目录失败: ${currentPath}, 错误码: ${createResult.err}` };
                }
            } else if (statResult.err === 0) {
                if (!statResult.data?.isDirectory?.()) {
                    return { success: false, error: `路径已存在但不是目录: ${currentPath}` };
                }
            } else {
                return { success: false, error: `检查路径状态失败: ${currentPath}, 错误码: ${statResult.err}` };
            }
        }
        
        return { success: true };
    } catch (error) {
        const errorMessage = error instanceof Error ? error.message : '未知错误';
        return { success: false, error: `递归创建目录异常: ${errorMessage}` };
    }
}