import api from "./api"

export const signup = async (data) => {
    const response = await api.post("/api/v1/auth/signup", {
        username: data.email,
        password: data.password,
        name: data.name,
        role: data.role,
        ...(data.companyId && { companyId: data.companyId })
    })
    
    // Store auth data in localStorage
    if (response.data.token) {
        localStorage.setItem("token", response.data.token)
        localStorage.setItem("userRole", response.data.user.role)
        localStorage.setItem("userId", response.data.user.id)
        
        // Store company ID if available (for internal auditors and managers)
        if (response.data.user.companyId) {
            localStorage.setItem("companyId", response.data.user.companyId.toString())
        }
    }
    
    return response.data
}

export const login = async (data) => {
    const response = await api.post("/api/v1/auth/login", {
        username: data.email,
        password: data.password
    })
    if (response.data.token) {
        localStorage.setItem("token", response.data.token)
        localStorage.setItem("userRole", response.data.user.role)
        localStorage.setItem("userId", response.data.user.id)

        // Store company ID if available (for internal auditors and managers)
        if (response.data.user.companyId) {
            localStorage.setItem("companyId", response.data.user.companyId.toString())
        }
    }
    return response.data
}

export const logout = () => {
    // Don't remove companyId - keep it for next login
    localStorage.removeItem("token")
    localStorage.removeItem("userRole")
    localStorage.removeItem("userId")
    window.location.href = "/"
}

export const getCurrentUser = () => {
    const token = localStorage.getItem("token")
    const userRole = localStorage.getItem("userRole")
    const userId = localStorage.getItem("userId")

    if (!token || !userRole || !userId) return null

    return {
        id: userId,
        role: userRole,
        token: token
    }
}

export const isAuthenticated = () => {
    return !!localStorage.getItem("token")
}
